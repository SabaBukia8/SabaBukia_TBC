package com.example.mtgcollectionmanager.data.sync

import com.example.mtgcollectionmanager.data.local.dao.CollectionCardDao
import com.example.mtgcollectionmanager.data.local.dao.CollectionDao
import com.example.mtgcollectionmanager.data.mapper.toEntity
import com.example.mtgcollectionmanager.data.remote.firebase.FirestoreDataSource
import com.example.mtgcollectionmanager.data.remote.firebase.dto.FirestoreCardDto
import com.example.mtgcollectionmanager.data.remote.firebase.dto.FirestoreCollectionDto
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manager responsible for synchronizing collection data between Firestore and local database
 */
@Singleton
class CollectionSyncManager @Inject constructor(
    private val collectionDao: CollectionDao,
    private val collectionCardDao: CollectionCardDao,
    private val firestoreDataSource: FirestoreDataSource
) {
    /**
     * Syncs collections from Firestore to local database
     */
    suspend fun syncCollections(userId: String): List<FirestoreCollectionDto> {
        val remote = firestoreDataSource.getCollectionsOnce(userId)
        if (remote.isNotEmpty()) {
            collectionDao.deleteAllCollectionsForUser(userId)
            remote.forEach { dto ->
                collectionDao.insertCollection(dto.toEntity(userId))
            }
        }
        return remote
    }

    /**
     * Syncs cards for a specific collection from Firestore to local database
     */
    suspend fun syncCardsForCollection(userId: String, collectionId: String, localCollectionId: Long): List<FirestoreCardDto> {
        val remoteCards = firestoreDataSource.getCardsOnce(userId, collectionId)
        
        collectionCardDao.deleteAllCardsForCollection(localCollectionId, userId)
        
        remoteCards.forEach { dto ->
            val entity = dto.toEntity(localCollectionId, userId)
            collectionCardDao.insertCard(entity)
        }
        
        return remoteCards
    }

    /**
     * Syncs a specific collection from Firestore to local database
     */
    suspend fun syncCollection(userId: String, firestoreId: String): FirestoreCollectionDto? {
        val remoteCollection = firestoreDataSource.getCollectionById(userId, firestoreId) ?: return null
        
        val entity = remoteCollection.toEntity(userId)
        collectionDao.insertCollection(entity)
        
        return remoteCollection
    }

    /**
     * Calculates statistics for a collection
     */
    suspend fun calculateCollectionStats(userId: String, collectionId: String): CollectionStats {
        val cards = firestoreDataSource.getCardsOnce(userId, collectionId)
        return CollectionStats(
            totalCards = cards.sumOf { it.quantity },
            totalValue = cards.sumOf { it.price * it.quantity }
        )
    }

    data class CollectionStats(
        val totalCards: Int,
        val totalValue: Double
    )
}