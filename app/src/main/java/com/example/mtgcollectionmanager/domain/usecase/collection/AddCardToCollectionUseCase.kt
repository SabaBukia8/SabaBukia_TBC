package com.example.mtgcollectionmanager.domain.usecase.collection

import com.example.mtgcollectionmanager.domain.common.Resource
import com.example.mtgcollectionmanager.domain.model.Card
import com.example.mtgcollectionmanager.domain.model.CardCondition
import com.example.mtgcollectionmanager.domain.repository.CollectionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AddCardToCollectionUseCase @Inject constructor(
    private val repository: CollectionRepository
) {
    suspend operator fun invoke(
        collectionId: Long,
        card: Card,
        quantity: Int,
        condition: CardCondition,
        notes: String
    ): Flow<Resource<Unit>> =
        repository.addCard(collectionId, card, quantity, condition, notes)
}
