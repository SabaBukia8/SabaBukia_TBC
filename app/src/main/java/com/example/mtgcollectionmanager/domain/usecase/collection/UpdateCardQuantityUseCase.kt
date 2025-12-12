package com.example.mtgcollectionmanager.domain.usecase.collection

import com.example.mtgcollectionmanager.domain.common.Resource
import com.example.mtgcollectionmanager.domain.repository.CollectionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UpdateCardQuantityUseCase @Inject constructor(
    private val repository: CollectionRepository
) {
    suspend operator fun invoke(cardId: String, quantity: Int): Flow<Resource<Unit>> =
        repository.updateCardQuantity(cardId, quantity)
}
