package com.example.mtgcollectionmanager.data.repository

import com.example.mtgcollectionmanager.data.local.dao.CollectionCardDao
import com.example.mtgcollectionmanager.data.local.dao.CollectionDao
import com.example.mtgcollectionmanager.data.mapper.toDomain
import com.example.mtgcollectionmanager.data.mapper.toEntity
import com.example.mtgcollectionmanager.data.model.local.CollectionEntity
import com.example.mtgcollectionmanager.domain.common.Resource
import com.example.mtgcollectionmanager.domain.model.Collection
import com.example.mtgcollectionmanager.domain.repository.AuthRepository
import com.example.mtgcollectionmanager.domain.repository.UserCollectionsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserCollectionsRepositoryImpl @Inject constructor(
    private val collectionDao: CollectionDao,
    private val collectionCardDao: CollectionCardDao,
    private val authRepository: AuthRepository
) : UserCollectionsRepository {

    private val userId: String
        get() = authRepository.getCurrentUser()?.uid ?: ""

    override fun getAllCollections(): Flow<Resource<List<Collection>>> = flow {
        emit(Resource.Loading(true))
        emit(Resource.Loading(false))
        try {
            collectionDao.getAllCollections(userId).collect { entities ->
                // Get statistics for each collection
                val collections = entities.map { entity ->
                    val totalCards = collectionCardDao.getTotalCardCount(entity.id, userId) ?: 0
                    val totalValue = collectionCardDao.getTotalValue(entity.id, userId) ?: 0.0
                    entity.toDomain(totalCards, totalValue)
                }
                emit(Resource.Success(collections))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to load collections"))
        }
    }

    override fun getCollectionById(collectionId: Long): Flow<Resource<Collection?>> = flow {
        emit(Resource.Loading(true))
        emit(Resource.Loading(false))
        try {
            collectionDao.getCollectionByIdFlow(collectionId, userId).collect { entity ->
                if (entity != null) {
                    val totalCards = collectionCardDao.getTotalCardCount(entity.id, userId) ?: 0
                    val totalValue = collectionCardDao.getTotalValue(entity.id, userId) ?: 0.0
                    emit(Resource.Success(entity.toDomain(totalCards, totalValue)))
                } else {
                    emit(Resource.Success(null))
                }
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to load collection"))
        }
    }

    override suspend fun createCollection(
        name: String,
        description: String
    ): Flow<Resource<Long>> = flow {
        emit(Resource.Loading(true))
        try {
            val entity = CollectionEntity(
                name = name,
                description = description,
                createdDate = System.currentTimeMillis(),
                userId = userId
            )
            val id = collectionDao.insertCollection(entity)
            emit(Resource.Success(id))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to create collection"))
        } finally {
            emit(Resource.Loading(false))
        }
    }

    override suspend fun updateCollection(collection: Collection): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading(true))
        try {
            collectionDao.updateCollection(collection.toEntity())
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to update collection"))
        } finally {
            emit(Resource.Loading(false))
        }
    }

    override suspend fun deleteCollection(collectionId: Long): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading(true))
        try {
            collectionDao.deleteCollectionById(collectionId, userId)
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to delete collection"))
        } finally {
            emit(Resource.Loading(false))
        }
    }

    override suspend fun getCollectionCount(): Int =
        collectionDao.getCollectionCount(userId)
}
