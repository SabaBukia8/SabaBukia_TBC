package com.example.mtgcollectionmanager.domain.repository

import com.example.mtgcollectionmanager.domain.common.Resource
import com.example.mtgcollectionmanager.domain.model.Card
import kotlinx.coroutines.flow.Flow

interface CardRepository {
    suspend fun searchCards(query: String): Flow<Resource<List<Card>>>
    suspend fun getCardById(cardId: String): Flow<Resource<Card>>
    fun getCardPrintings(cardName: String): Flow<Resource<List<Card>>>
}
