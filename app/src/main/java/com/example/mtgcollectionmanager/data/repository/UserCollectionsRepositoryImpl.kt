package com.example.mtgcollectionmanager.data.repository

import com.example.mtgcollectionmanager.data.local.dao.CollectionCardDao
import com.example.mtgcollectionmanager.data.local.dao.CollectionDao
import com.example.mtgcollectionmanager.data.mapper.toDomain
import com.example.mtgcollectionmanager.data.mapper.toEntity
import com.example.mtgcollectionmanager.data.model.local.CollectionEntity
import com.example.mtgcollectionmanager.data.remote.firebase.FirestoreDataSource
import com.example.mtgcollectionmanager.data.remote.firebase.dto.FirestoreCollectionDto
import com.example.mtgcollectionmanager.domain.common.Resource
import com.example.mtgcollectionmanager.domain.model.Collection
import com.example.mtgcollectionmanager.domain.repository.AuthRepository
import com.example.mtgcollectionmanager.domain.repository.UserCollectionsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserCollectionsRepositoryImpl @Inject constructor(
    private val collectionDao: CollectionDao,
    private val collectionCardDao: CollectionCardDao,
    private val firestoreDataSource: FirestoreDataSource,
    private val authRepository: AuthRepository
) : UserCollectionsRepository {

    private val userId: String
        get() = authRepository.getCurrentUser()?.uid ?: ""

    override fun getAllCollections(): Flow<Resource<List<Collection>>> = flow {
        emit(Resource.Loading(true))
        try {
            val firestoreCollections = firestoreDataSource.getCollectionsOnce(userId)

            collectionDao.deleteAllCollectionsForUser(userId)
            collectionCardDao.deleteAllCards(userId)

            firestoreCollections.forEach { dto ->
                val entity = dto.toEntity(userId)
                collectionDao.insertCollection(entity)
            }

            val collections = firestoreCollections.map { dto ->
                val entity = dto.toEntity(userId)
                val totalCards = collectionCardDao.getTotalCardCount(entity.id, userId) ?: 0
                val totalValue = collectionCardDao.getTotalValue(entity.id, userId) ?: 0.0
                entity.toDomain(totalCards, totalValue)
            }
            emit(Resource.Success(collections))
            emit(Resource.Loading(false))
        } catch (e: Exception) {
            emit(Resource.Loading(false))
            try {
                collectionDao.getAllCollections(userId).collect { entities ->
                    val collections = entities.map { entity ->
                        val totalCards = collectionCardDao.getTotalCardCount(entity.id, userId) ?: 0
                        val totalValue = collectionCardDao.getTotalValue(entity.id, userId) ?: 0.0
                        entity.toDomain(totalCards, totalValue)
                    }
                    emit(Resource.Success(collections))
                }
            } catch (cacheError: Exception) {
                emit(Resource.Error(e.message ?: "Failed to load collections"))
            }
        }
    }

    override fun getCollectionById(collectionId: Long): Flow<Resource<Collection?>> = flow {
        emit(Resource.Loading(true))
        try {
            val firestoreCollection =
                firestoreDataSource.getCollectionById(userId, collectionId.toString())

            if (firestoreCollection != null) {
                val entity = firestoreCollection.toEntity(userId)
                collectionDao.insertCollection(entity)

                val totalCards = collectionCardDao.getTotalCardCount(entity.id, userId) ?: 0
                val totalValue = collectionCardDao.getTotalValue(entity.id, userId) ?: 0.0
                emit(Resource.Success(entity.toDomain(totalCards, totalValue)))
            } else {
                val localEntity = collectionDao.getCollectionById(collectionId, userId)
                if (localEntity != null) {
                    val totalCards =
                        collectionCardDao.getTotalCardCount(localEntity.id, userId) ?: 0
                    val totalValue = collectionCardDao.getTotalValue(localEntity.id, userId) ?: 0.0
                    emit(Resource.Success(localEntity.toDomain(totalCards, totalValue)))
                } else {
                    emit(Resource.Success(null))
                }
            }
            emit(Resource.Loading(false))
        } catch (e: Exception) {
            emit(Resource.Loading(false))
            try {
                val localEntity = collectionDao.getCollectionById(collectionId, userId)
                if (localEntity != null) {
                    val totalCards =
                        collectionCardDao.getTotalCardCount(localEntity.id, userId) ?: 0
                    val totalValue = collectionCardDao.getTotalValue(localEntity.id, userId) ?: 0.0
                    emit(Resource.Success(localEntity.toDomain(totalCards, totalValue)))
                } else {
                    emit(Resource.Success(null))
                }
            } catch (cacheError: Exception) {
                emit(Resource.Error(e.message ?: "Failed to load collection"))
            }
        }
    }

    override suspend fun createCollection(
        name: String,
        description: String
    ): Flow<Resource<Long>> = flow {
        emit(Resource.Loading(true))
        try {
            val dto = FirestoreCollectionDto(
                name = name,
                description = description,
                createdAt = System.currentTimeMillis()
            )

            val firestoreId = firestoreDataSource.createCollection(userId, dto)

            val entity = CollectionEntity(
                id = firestoreId.hashCode().toLong().let { if (it < 0) -it else it },
                name = name,
                description = description,
                createdDate = dto.createdAt,
                userId = userId
            )
            val localId = collectionDao.insertCollection(entity)

            emit(Resource.Success(localId))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to create collection"))
        } finally {
            emit(Resource.Loading(false))
        }
    }

    override suspend fun updateCollection(collection: Collection): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading(true))
        try {
            firestoreDataSource.updateCollection(
                userId,
                collection.id.toString(),
                mapOf(
                    "name" to collection.name,
                    "description" to collection.description
                )
            )

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
            firestoreDataSource.deleteCollection(userId, collectionId.toString())

            collectionDao.deleteCollectionById(collectionId, userId)

            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to delete collection"))
        } finally {
            emit(Resource.Loading(false))
        }
    }

    override suspend fun getCollectionCount(): Int = collectionDao.getCollectionCount(userId)
}
