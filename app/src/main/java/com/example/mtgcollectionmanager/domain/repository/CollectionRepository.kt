package com.example.mtgcollectionmanager.domain.repository

import com.example.mtgcollectionmanager.domain.common.Resource
import com.example.mtgcollectionmanager.domain.model.Card
import com.example.mtgcollectionmanager.domain.model.CardCondition
import com.example.mtgcollectionmanager.domain.model.CollectionCard
import kotlinx.coroutines.flow.Flow

interface CollectionRepository {
    suspend fun getCollectionCards(collectionId: Long): Flow<Resource<List<CollectionCard>>>
    suspend fun getCardsByCategory(
        collectionId: Long,
        categoryId: Long?
    ): Flow<Resource<List<CollectionCard>>>

    suspend fun getCardsByColor(
        collectionId: Long,
        color: String
    ): Flow<Resource<List<CollectionCard>>>

    suspend fun getCardsBySet(
        collectionId: Long,
        setCode: String
    ): Flow<Resource<List<CollectionCard>>>

    suspend fun addCard(
        collectionId: Long,
        card: Card,
        quantity: Int,
        condition: CardCondition,
        notes: String
    ): Flow<Resource<Unit>>

    suspend fun removeCard(collectionId: Long, cardId: String): Flow<Resource<Unit>>
    suspend fun updateCardQuantity(
        collectionId: Long,
        cardId: String,
        quantity: Int
    ): Flow<Resource<Unit>>

    suspend fun updateCardDetails(
        collectionId: Long,
        cardId: String,
        quantity: Int,
        condition: CardCondition,
        notes: String
    ): Flow<Resource<Unit>>

    suspend fun isCardInCollection(collectionId: Long, cardId: String): Boolean
}
