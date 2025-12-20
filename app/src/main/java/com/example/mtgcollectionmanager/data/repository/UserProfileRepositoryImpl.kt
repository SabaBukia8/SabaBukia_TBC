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
import com.example.mtgcollectionmanager.domain.model.UserProfile
import com.example.mtgcollectionmanager.domain.repository.UserProfileRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserProfileRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestoreDataSource: FirestoreDataSource,
    private val collectionDao: CollectionDao,
    private val collectionCardDao: CollectionCardDao,
    private val collectionSyncManager: CollectionSyncManager,
    private val cardSyncManager: CardSyncManager
) : UserProfileRepository {

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

    private suspend fun getOrFixTotalCardCount(uid: String): Int {
        val allCards = collectionCardDao.getAllCardsForUser(uid)
        return allCards.sumOf { card ->
            if (card.quantity > 0) card.quantity else 1
        }
    }

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
                }
            }
        } catch (_: Exception) {
        }
    }

}
