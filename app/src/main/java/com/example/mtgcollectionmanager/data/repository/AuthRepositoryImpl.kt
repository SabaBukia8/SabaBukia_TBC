package com.example.mtgcollectionmanager.data.repository

import com.example.mtgcollectionmanager.data.local.dao.CategoryDao
import com.example.mtgcollectionmanager.data.local.dao.CollectionCardDao
import com.example.mtgcollectionmanager.data.local.dao.CollectionDao
import com.example.mtgcollectionmanager.data.remote.firebase.FirestoreDataSource
import com.example.mtgcollectionmanager.data.remote.firebase.dto.FirestoreUserProfileDto
import com.example.mtgcollectionmanager.domain.common.Resource
import com.example.mtgcollectionmanager.domain.model.User
import com.example.mtgcollectionmanager.domain.model.UserProfile
import com.example.mtgcollectionmanager.domain.repository.AuthRepository
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestoreDataSource: FirestoreDataSource,
    private val collectionDao: CollectionDao,
    private val collectionCardDao: CollectionCardDao,
    private val categoryDao: CategoryDao
) : AuthRepository {

    override suspend fun login(email: String, password: String): Flow<Resource<User>> = flow {
        emit(Resource.Loading(true))
        try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            result.user?.let { firebaseUser ->
                emit(Resource.Success(firebaseUser.toUser()))
            } ?: emit(Resource.Error("Login failed"))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Unknown error"))
        } finally {
            emit(Resource.Loading(false))
        }
    }

    override suspend fun register(
        email: String,
        password: String,
        nickname: String
    ): Flow<Resource<User>> = flow {
        emit(Resource.Loading(true))
        try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            result.user?.let { firebaseUser ->
                // Create user profile in Firestore
                val profile = FirestoreUserProfileDto(
                    nickname = nickname.ifBlank { email.substringBefore("@") },
                    email = email,
                    createdAt = System.currentTimeMillis()
                )
                firestoreDataSource.createUserProfile(firebaseUser.uid, profile)
                emit(Resource.Success(firebaseUser.toUser(nickname)))
            } ?: emit(Resource.Error("Registration failed"))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Unknown error"))
        } finally {
            emit(Resource.Loading(false))
        }
    }

    override fun getCurrentUser(): User? = firebaseAuth.currentUser?.toUser()

    override fun isUserLoggedIn(): Boolean = firebaseAuth.currentUser != null

    override fun logout() {
        firebaseAuth.signOut()
    }

    override suspend fun getUserProfile(): Flow<Resource<UserProfile>> = flow {
        emit(Resource.Loading(true))
        try {
            val user = firebaseAuth.currentUser
            if (user == null) {
                emit(Resource.Error("User not logged in"))
                return@flow
            }

            val profile = firestoreDataSource.getUserProfile(user.uid)
            if (profile != null) {
                val collectionCount = collectionDao.getCollectionCount(user.uid)
                val cardRecordCount = collectionCardDao.getCardRecordCountForUser(user.uid)
                var totalCardsCount = collectionCardDao.getTotalCardCountForUser(user.uid)

                // If we have card records but totalCards is 0, something's wrong with quantity values
                if (cardRecordCount > 0 && totalCardsCount == 0) {
                    // Force all card quantities to be at least 1
                    val allCards = collectionCardDao.getAllCardsForUser(user.uid)
                    allCards.forEach { card ->
                        if (card.quantity <= 0) {
                            collectionCardDao.updateCard(card.copy(quantity = 1))
                        }
                    }
                    // Recalculate the total
                    val updatedTotal = collectionCardDao.getTotalCardCountForUser(user.uid)
                    if (updatedTotal > 0) {
                        totalCardsCount = updatedTotal
                    }
                }

                // Debug: Let's force a refresh of card data from Firestore
                try {
                    // Get all collections
                    val collections = firestoreDataSource.getCollectionsOnce(user.uid)

                    // For each collection, get all cards and save them locally
                    var cardCount = 0
                    collections.forEach { collection ->
                        val cards = firestoreDataSource.getCardsOnce(user.uid, collection.id)
                        cards.forEach { card ->
                            // Convert Firestore ID to local ID
                            val localId =
                                collection.id.hashCode().toLong().let { if (it < 0) -it else it }
                            val entity = card.toEntity(localId, user.uid)
                            collectionCardDao.insertCard(entity)
                            cardCount += card.quantity
                        }
                    }

                    // Use the card count we just calculated
                    if (cardCount > 0) {
                        emit(
                            Resource.Success(
                                UserProfile(
                                    uid = user.uid,
                                    email = profile.email,
                                    nickname = profile.nickname,
                                    createdAt = profile.createdAt,
                                    collectionCount = collectionCount,
                                    totalCards = cardCount
                                )
                            )
                        )
                        emit(Resource.Loading(false))
                        return@flow
                    }
                } catch (e: Exception) {
                    // If there's an error, continue with the original method
                }

                emit(
                    Resource.Success(
                        UserProfile(
                            uid = user.uid,
                            email = profile.email,
                            nickname = profile.nickname,
                            createdAt = profile.createdAt,
                            collectionCount = collectionCount,
                            totalCards = totalCardsCount
                        )
                    )
                )
            } else {
                val newProfile = FirestoreUserProfileDto(
                    nickname = user.displayName ?: user.email?.substringBefore("@") ?: "User",
                    email = user.email ?: "",
                    createdAt = user.metadata?.creationTimestamp ?: System.currentTimeMillis()
                )
                firestoreDataSource.createUserProfile(user.uid, newProfile)

                emit(
                    Resource.Success(
                        UserProfile(
                            uid = user.uid,
                            email = newProfile.email,
                            nickname = newProfile.nickname,
                            createdAt = newProfile.createdAt,
                            collectionCount = 0,
                            totalCards = 0
                        )
                    )
                )
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to get profile"))
        } finally {
            emit(Resource.Loading(false))
        }
    }

    override suspend fun updateNickname(nickname: String): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading(true))
        try {
            val user = firebaseAuth.currentUser
            if (user == null) {
                emit(Resource.Error("User not logged in"))
                return@flow
            }

            firestoreDataSource.updateUserProfile(user.uid, mapOf("nickname" to nickname))
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to update nickname"))
        } finally {
            emit(Resource.Loading(false))
        }
    }

    override suspend fun changePassword(
        currentPassword: String,
        newPassword: String
    ): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading(true))
        try {
            val user = firebaseAuth.currentUser
            if (user == null || user.email == null) {
                emit(Resource.Error("User not logged in"))
                return@flow
            }

            val credential = EmailAuthProvider.getCredential(user.email!!, currentPassword)
            user.reauthenticate(credential).await()

            user.updatePassword(newPassword).await()
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to change password"))
        } finally {
            emit(Resource.Loading(false))
        }
    }

    override suspend fun deleteAccount(password: String): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading(true))
        try {
            val user = firebaseAuth.currentUser
            if (user == null || user.email == null) {
                emit(Resource.Error("User not logged in"))
                return@flow
            }

            val credential = EmailAuthProvider.getCredential(user.email!!, password)
            user.reauthenticate(credential).await()

            firestoreDataSource.deleteUserData(user.uid)

            collectionDao.deleteAllCollectionsForUser(user.uid)

            user.delete().await()

            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to delete account"))
        } finally {
            emit(Resource.Loading(false))
        }
    }

    private fun FirebaseUser.toUser(nickname: String? = null) = User(
        uid = uid,
        email = email ?: "",
        displayName = nickname ?: displayName ?: email?.substringBefore("@") ?: "User"
    )
}
