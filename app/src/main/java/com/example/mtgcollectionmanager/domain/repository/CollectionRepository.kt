package com.example.mtgcollectionmanager.domain.repository

import com.example.mtgcollectionmanager.domain.common.Resource
import com.example.mtgcollectionmanager.domain.model.Card
import com.example.mtgcollectionmanager.domain.model.CardCondition
import com.example.mtgcollectionmanager.domain.model.CollectionCard
import kotlinx.coroutines.flow.Flow

interface CollectionRepository {
    fun getCollectionCards(): Flow<Resource<List<CollectionCard>>>
    fun getCardsByColor(color: String): Flow<Resource<List<CollectionCard>>>
    fun getCardsBySet(setCode: String): Flow<Resource<List<CollectionCard>>>
    suspend fun addCard(card: Card, quantity: Int, condition: CardCondition, notes: String): Flow<Resource<Unit>>
    suspend fun removeCard(cardId: String): Flow<Resource<Unit>>
    suspend fun updateCardQuantity(cardId: String, quantity: Int): Flow<Resource<Unit>>
    suspend fun isCardInCollection(cardId: String): Boolean
}
