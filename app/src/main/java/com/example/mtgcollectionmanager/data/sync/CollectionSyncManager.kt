package com.example.mtgcollectionmanager.data.sync

import com.example.mtgcollectionmanager.data.local.dao.CollectionCardDao
import com.example.mtgcollectionmanager.data.local.dao.CollectionDao
import com.example.mtgcollectionmanager.data.mapper.toEntity
import com.example.mtgcollectionmanager.data.remote.firebase.FirestoreDataSource
import com.example.mtgcollectionmanager.data.remote.firebase.dto.FirestoreCardDto
import com.example.mtgcollectionmanager.data.remote.firebase.dto.FirestoreCollectionDto
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CollectionSyncManager @Inject constructor(
    private val collectionDao: CollectionDao,
    private val collectionCardDao: CollectionCardDao,
    private val firestoreDataSource: FirestoreDataSource
) {
    suspend fun syncCollections(userId: String): List<FirestoreCollectionDto> {
        val remote = firestoreDataSource.getCollectionsOnce(userId)

        if (remote.isNotEmpty()) {
            val entities = remote.map { dto ->
                dto.toEntity(userId)
            }

            collectionDao.deleteAllCollectionsForUser(userId)
            entities.forEach { entity ->
                collectionDao.insertCollection(entity)
            }
        }
        return remote
    }

    suspend fun syncCardsForCollection(userId: String, collectionId: String, localCollectionId: Long): List<FirestoreCardDto> {
        val remoteCards = firestoreDataSource.getCardsOnce(userId, collectionId)

        if (remoteCards.isNotEmpty()) {
            val entities = remoteCards.map { dto ->
                dto.toEntity(localCollectionId, userId)
            }

            collectionCardDao.deleteAllCardsForCollection(localCollectionId, userId)
            entities.forEach { entity ->
                collectionCardDao.insertCard(entity)
            }
        }
        
        return remoteCards
    }

    suspend fun syncCollection(userId: String, firestoreId: String): FirestoreCollectionDto? {
        val remoteCollection = firestoreDataSource.getCollectionById(userId, firestoreId) ?: return null
        
        val entity = remoteCollection.toEntity(userId)
        collectionDao.insertCollection(entity)
        
        return remoteCollection
    }

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