package com.example.mtgcollectionmanager.domain.repository

import com.example.mtgcollectionmanager.domain.common.Resource
import com.example.mtgcollectionmanager.domain.model.Collection
import kotlinx.coroutines.flow.Flow

interface UserCollectionsRepository {
    fun getAllCollections(): Flow<Resource<List<Collection>>>
    fun getCollectionById(collectionId: Long): Flow<Resource<Collection?>>
    suspend fun createCollection(name: String, description: String): Flow<Resource<Long>>
    suspend fun updateCollection(collection: Collection): Flow<Resource<Unit>>
    suspend fun deleteCollection(collectionId: Long): Flow<Resource<Unit>>
    suspend fun getCollectionCount(): Int
}
