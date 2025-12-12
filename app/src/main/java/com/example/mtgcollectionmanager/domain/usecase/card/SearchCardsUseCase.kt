package com.example.mtgcollectionmanager.domain.usecase.card

import com.example.mtgcollectionmanager.domain.common.Resource
import com.example.mtgcollectionmanager.domain.model.Card
import com.example.mtgcollectionmanager.domain.repository.CardRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchCardsUseCase @Inject constructor(
    private val repository: CardRepository
) {
    suspend operator fun invoke(query: String): Flow<Resource<List<Card>>> =
        repository.searchCards(query)
}
