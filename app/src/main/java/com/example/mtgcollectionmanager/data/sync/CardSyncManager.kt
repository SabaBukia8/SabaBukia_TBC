package com.example.mtgcollectionmanager.data.sync

import com.example.mtgcollectionmanager.data.local.dao.CollectionCardDao
import com.example.mtgcollectionmanager.data.mapper.toEntity
import com.example.mtgcollectionmanager.data.remote.firebase.FirestoreDataSource
import com.example.mtgcollectionmanager.data.remote.firebase.dto.FirestoreCardDto
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class CardSyncManager @Inject constructor(
    private val cardDao: CollectionCardDao,
    private val firestoreDataSource: FirestoreDataSource
) {
    suspend fun syncCards(userId: String, firestoreId: String, localCollectionId: Long): List<FirestoreCardDto> {

        val remoteCards = firestoreDataSource.getCardsOnce(userId, firestoreId)

        val entities = remoteCards.map { dto ->
            dto.toEntity(localCollectionId, userId)
        }

        if (entities.isNotEmpty()) {
            cardDao.deleteAllCardsForCollection(localCollectionId, userId)

            entities.forEach { entity ->
                cardDao.insertCard(entity)
            }
        }
        
        return remoteCards
    }

    suspend fun updateCard(userId: String, firestoreId: String, cardId: String, updates: Map<String, Any?>): Boolean {
        val firestoreCard = firestoreDataSource.getCardByCardId(userId, firestoreId, cardId) ?: return false
        firestoreDataSource.updateCard(userId, firestoreId, firestoreCard.id, updates)
        return true
    }

    suspend fun addCard(userId: String, firestoreId: String, card: FirestoreCardDto): String {
        return firestoreDataSource.addCard(userId, firestoreId, card)
    }

    suspend fun deleteCard(userId: String, firestoreId: String, cardId: String): Boolean {
        val firestoreCard = firestoreDataSource.getCardByCardId(userId, firestoreId, cardId) ?: return false
        firestoreDataSource.deleteCard(userId, firestoreId, firestoreCard.id)
        return true
    }
}