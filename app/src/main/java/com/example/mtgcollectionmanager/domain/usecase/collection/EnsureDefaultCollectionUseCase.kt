package com.example.mtgcollectionmanager.domain.usecase.collection

import com.example.mtgcollectionmanager.domain.common.Resource
import com.example.mtgcollectionmanager.domain.repository.UserCollectionsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class EnsureDefaultCollectionUseCase @Inject constructor(
    private val userCollectionsRepository: UserCollectionsRepository
) {
    suspend operator fun invoke(): Flow<Resource<Long>> = flow {
        emit(Resource.Loading(true))

        try {
            val collectionCount = userCollectionsRepository.getCollectionCount()

            if (collectionCount == 0) {
                // Create default collection
                userCollectionsRepository.createCollection(
                    name = "My Collection",
                    description = "My first card collection"
                ).collect { resource ->
                    when (resource) {
                        is Resource.Success -> emit(Resource.Success(resource.data))
                        is Resource.Error -> emit(Resource.Error(resource.errorMessage))
                        is Resource.Loading -> emit(Resource.Loading(resource.isLoading))
                    }
                }
            } else {
                // Default collection already exists, return success with ID 1
                emit(Resource.Success(1L))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to ensure default collection"))
        } finally {
            emit(Resource.Loading(false))
        }
    }
}
