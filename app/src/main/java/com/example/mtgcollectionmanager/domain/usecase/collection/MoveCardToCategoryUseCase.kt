package com.example.mtgcollectionmanager.domain.usecase.collection

import com.example.mtgcollectionmanager.domain.common.Resource
import com.example.mtgcollectionmanager.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MoveCardToCategoryUseCase @Inject constructor(
    private val categoryRepository: CategoryRepository
) {
    suspend operator fun invoke(
        collectionId: Long,
        scryfallCardId: String,
        categoryId: Long?
    ): Flow<Resource<Unit>> {
        return categoryRepository.moveCardToCategory(collectionId, scryfallCardId, categoryId)
    }
}
