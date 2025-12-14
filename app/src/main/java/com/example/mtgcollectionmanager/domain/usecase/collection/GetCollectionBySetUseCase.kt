package com.example.mtgcollectionmanager.domain.usecase.collection

import com.example.mtgcollectionmanager.domain.common.Resource
import com.example.mtgcollectionmanager.domain.model.CollectionCard
import com.example.mtgcollectionmanager.domain.repository.CollectionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCollectionBySetUseCase @Inject constructor(
    private val repository: CollectionRepository
) {
    operator fun invoke(collectionId: Long, setCode: String): Flow<Resource<List<CollectionCard>>> =
        repository.getCardsBySet(collectionId, setCode)
}
