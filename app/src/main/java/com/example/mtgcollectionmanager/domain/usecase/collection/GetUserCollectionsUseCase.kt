package com.example.mtgcollectionmanager.domain.usecase.collection

import com.example.mtgcollectionmanager.domain.common.Resource
import com.example.mtgcollectionmanager.domain.model.Collection
import com.example.mtgcollectionmanager.domain.repository.UserCollectionsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserCollectionsUseCase @Inject constructor(
    private val userCollectionsRepository: UserCollectionsRepository
) {
    operator fun invoke(): Flow<Resource<List<Collection>>> {
        return userCollectionsRepository.getAllCollections()
    }
}
