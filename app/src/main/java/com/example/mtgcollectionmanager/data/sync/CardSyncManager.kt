package com.example.mtgcollectionmanager.data.sync

import com.example.mtgcollectionmanager.data.local.dao.CollectionCardDao
import com.example.mtgcollectionmanager.data.mapper.toEntity
import com.example.mtgcollectionmanager.data.remote.firebase.FirestoreDataSource
import com.example.mtgcollectionmanager.data.remote.firebase.dto.FirestoreCardDto
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manager responsible for synchronizing card data between Firestore and local database
 */
@Singleton
class CardSyncManager @Inject constructor(
    private val cardDao: CollectionCardDao,
    private val firestoreDataSource: FirestoreDataSource
) {
    /**
     * Syncs cards for a specific collection from Firestore to local database
     */
    suspend fun syncCards(userId: String, firestoreId: String, localCollectionId: Long): List<FirestoreCardDto> {
        val remoteCards = firestoreDataSource.getCardsOnce(userId, firestoreId)
        
        cardDao.deleteAllCardsForCollection(localCollectionId, userId)
        
        remoteCards.forEach { dto ->
            val entity = dto.toEntity(localCollectionId, userId)
            cardDao.insertCard(entity)
        }
        
        return remoteCards
    }

    /**
     * Updates a card in Firestore
     */
    suspend fun updateCard(userId: String, firestoreId: String, cardId: String, updates: Map<String, Any?>): Boolean {
        val firestoreCard = firestoreDataSource.getCardByCardId(userId, firestoreId, cardId) ?: return false
        firestoreDataSource.updateCard(userId, firestoreId, firestoreCard.id, updates)
        return true
    }

    /**
     * Adds a card to Firestore
     */
    suspend fun addCard(userId: String, firestoreId: String, card: FirestoreCardDto): String {
        return firestoreDataSource.addCard(userId, firestoreId, card)
    }

    /**
     * Deletes a card from Firestore
     */
    suspend fun deleteCard(userId: String, firestoreId: String, cardId: String): Boolean {
        val firestoreCard = firestoreDataSource.getCardByCardId(userId, firestoreId, cardId) ?: return false
        firestoreDataSource.deleteCard(userId, firestoreId, firestoreCard.id)
        return true
    }
}