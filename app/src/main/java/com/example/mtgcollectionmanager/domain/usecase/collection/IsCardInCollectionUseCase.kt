package com.example.mtgcollectionmanager.domain.usecase.collection

import com.example.mtgcollectionmanager.domain.repository.CollectionRepository
import javax.inject.Inject

class IsCardInCollectionUseCase @Inject constructor(
    private val repository: CollectionRepository
) {
    suspend operator fun invoke(collectionId: Long, cardId: String): Boolean =
        repository.isCardInCollection(collectionId, cardId)
}
