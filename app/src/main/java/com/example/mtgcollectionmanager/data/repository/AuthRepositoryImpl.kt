package com.example.mtgcollectionmanager.data.repository

import com.example.mtgcollectionmanager.data.common.resourceFlow
import com.example.mtgcollectionmanager.data.local.dao.CollectionCardDao
import com.example.mtgcollectionmanager.data.local.dao.CollectionDao
import com.example.mtgcollectionmanager.data.remote.firebase.FirestoreDataSource
import com.example.mtgcollectionmanager.data.remote.firebase.dto.FirestoreUserProfileDto
import com.example.mtgcollectionmanager.data.sync.CardSyncManager
import com.example.mtgcollectionmanager.data.sync.CollectionSyncManager
import com.example.mtgcollectionmanager.domain.common.AppError
import com.example.mtgcollectionmanager.domain.common.Resource
import com.example.mtgcollectionmanager.domain.model.User
import com.example.mtgcollectionmanager.domain.model.UserProfile
import com.example.mtgcollectionmanager.domain.repository.AuthRepository
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestoreDataSource: FirestoreDataSource,
    private val collectionDao: CollectionDao,
    private val collectionCardDao: CollectionCardDao,
    private val collectionSyncManager: CollectionSyncManager,
    private val cardSyncManager: CardSyncManager
) : AuthRepository {

    override suspend fun login(email: String, password: String): Flow<Resource<User>> = resourceFlow {
        try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            result.user?.let { firebaseUser ->
                emit(Resource.Success(firebaseUser.toUser()))
            } ?: emit(Resource.Error(AppError.Auth.LoginFailed))
        } catch (_: FirebaseAuthInvalidCredentialsException) {
            emit(Resource.Error(AppError.Auth.InvalidCredentials))
        } catch (_: FirebaseAuthInvalidUserException) {
            emit(Resource.Error(AppError.Auth.InvalidCredentials))
        } catch (e: Exception) {
            emit(Resource.Error(AppError.Unknown(e.message)))
        }
    }

    override suspend fun register(
        email: String,
        password: String,
        nickname: String
    ): Flow<Resource<User>> = resourceFlow {
        try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            result.user?.let { firebaseUser ->
                val profile = FirestoreUserProfileDto(
                    nickname = nickname.ifBlank { email.substringBefore("@") },
                    email = email,
                    createdAt = System.currentTimeMillis()
                )
                firestoreDataSource.createUserProfile(firebaseUser.uid, profile)
                emit(Resource.Success(firebaseUser.toUser(nickname)))
            } ?: emit(Resource.Error(AppError.Auth.RegistrationFailed))
        } catch (_: FirebaseAuthUserCollisionException) {
            emit(Resource.Error(AppError.Auth.EmailAlreadyInUse))
        } catch (_: FirebaseAuthWeakPasswordException) {
            emit(Resource.Error(AppError.Auth.WeakPassword))
        } catch (e: Exception) {
            emit(Resource.Error(AppError.Unknown(e.message)))
        }
    }

    override fun getCurrentUser(): User? = firebaseAuth.currentUser?.toUser()

    override fun isUserLoggedIn(): Boolean = firebaseAuth.currentUser != null

    override fun logout() {
        firebaseAuth.signOut()
    }

    override suspend fun getUserProfile(): Flow<Resource<UserProfile>> = resourceFlow {
        try {
            val user = firebaseAuth.currentUser ?: run {
                emit(Resource.Error(AppError.Auth.UserNotLoggedIn))
                return@resourceFlow
            }

            val profile = firestoreDataSource.getUserProfile(user.uid)

            if (profile != null) {
                emit(Resource.Success(buildUserProfile(user.uid, profile)))
            } else {
                emit(Resource.Success(createNewUserProfile(user)))
            }
        } catch (_: Exception) {
            emit(Resource.Error(AppError.Profile.LoadFailed))
        }
    }

    override suspend fun updateNickname(nickname: String): Flow<Resource<Unit>> = resourceFlow {
        try {
            val user = firebaseAuth.currentUser ?: run {
                emit(Resource.Error(AppError.Auth.UserNotLoggedIn))
                return@resourceFlow
            }

            firestoreDataSource.updateUserProfile(user.uid, mapOf("nickname" to nickname))
            emit(Resource.Success(Unit))
        } catch (_: Exception) {
            emit(Resource.Error(AppError.Profile.NicknameUpdateFailed))
        }
    }

    override suspend fun changePassword(
        currentPassword: String,
        newPassword: String
    ): Flow<Resource<Unit>> = resourceFlow {
        try {
            val user = firebaseAuth.currentUser
            val email = user?.email

            if (user == null || email == null) {
                emit(Resource.Error(AppError.Auth.UserNotLoggedIn))
                return@resourceFlow
            }

            val credential = EmailAuthProvider.getCredential(email, currentPassword)
            user.reauthenticate(credential).await()
            user.updatePassword(newPassword).await()
            emit(Resource.Success(Unit))
        } catch (_: FirebaseAuthInvalidCredentialsException) {
            emit(Resource.Error(AppError.Auth.InvalidCredentials))
        } catch (_: FirebaseAuthWeakPasswordException) {
            emit(Resource.Error(AppError.Auth.WeakPassword))
        } catch (_: Exception) {
            emit(Resource.Error(AppError.Auth.PasswordChangeFailed))
        }
    }

    override suspend fun deleteAccount(password: String): Flow<Resource<Unit>> = resourceFlow {
        try {
            val user = firebaseAuth.currentUser
            val email = user?.email

            if (user == null || email == null) {
                emit(Resource.Error(AppError.Auth.UserNotLoggedIn))
                return@resourceFlow
            }

            val credential = EmailAuthProvider.getCredential(email, password)
            user.reauthenticate(credential).await()

            firestoreDataSource.deleteUserData(user.uid)
            collectionDao.deleteAllCollectionsForUser(user.uid)
            user.delete().await()

            emit(Resource.Success(Unit))
        } catch (_: FirebaseAuthInvalidCredentialsException) {
            emit(Resource.Error(AppError.Auth.InvalidCredentials))
        } catch (_: Exception) {
            emit(Resource.Error(AppError.Profile.DeleteFailed))
        }
    }

    private suspend fun buildUserProfile(
        uid: String,
        profile: FirestoreUserProfileDto
    ): UserProfile {
        syncAllCollectionsAndCards(uid)

        val collectionCount = collectionDao.getCollectionCount(uid)
        val totalCardsCount = getOrFixTotalCardCount(uid)

        return UserProfile(
            uid = uid,
            email = profile.email,
            nickname = profile.nickname,
            createdAt = profile.createdAt,
            collectionCount = collectionCount,
            totalCards = totalCardsCount
        )
    }

    private suspend fun getOrFixTotalCardCount(uid: String): Int {
        val allCards = collectionCardDao.getAllCardsForUser(uid)
        return allCards.sumOf { card ->
            if (card.quantity > 0) card.quantity else 1
        }
    }

    private suspend fun createNewUserProfile(user: FirebaseUser): UserProfile {
        val newProfile = FirestoreUserProfileDto(
            nickname = user.displayName ?: user.email?.substringBefore("@") ?: "User",
            email = user.email ?: "",
            createdAt = user.metadata?.creationTimestamp ?: System.currentTimeMillis()
        )
        firestoreDataSource.createUserProfile(user.uid, newProfile)

        syncAllCollectionsAndCards(user.uid)

        val collectionCount = collectionDao.getCollectionCount(user.uid)
        val totalCardsCount = getOrFixTotalCardCount(user.uid)

        return UserProfile(
            uid = user.uid,
            email = newProfile.email,
            nickname = newProfile.nickname,
            createdAt = newProfile.createdAt,
            collectionCount = collectionCount,
            totalCards = totalCardsCount
        )
    }

    private fun FirebaseUser.toUser(nickname: String? = null) = User(
        uid = uid,
        email = email ?: "",
        displayName = nickname ?: displayName ?: email?.substringBefore("@") ?: "User"
    )

    private suspend fun syncAllCollectionsAndCards(uid: String) {
        try {
            val remoteCollections = collectionSyncManager.syncCollections(uid)

            remoteCollections.forEach { collection ->
                try {
                    val localCollection = collectionDao.getCollectionByFirestoreId(collection.id, uid)
                    if (localCollection != null) {
                        cardSyncManager.syncCards(uid, collection.id, localCollection.id)
                    }
                } catch (_: Exception) {
                    // Ignore individual collection sync failures
                }
            }
        } catch (_: Exception) {
            // Don't throw - we'll just use whatever is in the local database
        }
    }
}