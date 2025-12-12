package com.example.mtgcollectionmanager.domain.usecase.collection

import com.example.mtgcollectionmanager.domain.common.Resource
import com.example.mtgcollectionmanager.domain.repository.UserCollectionsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DeleteCollectionUseCase @Inject constructor(
    private val userCollectionsRepository: UserCollectionsRepository
) {
    suspend operator fun invoke(collectionId: Long): Flow<Resource<Unit>> {
        return userCollectionsRepository.deleteCollection(collectionId)
    }
}
