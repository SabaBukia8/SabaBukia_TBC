package com.example.mtgcollectionmanager.data.repository

import com.example.mtgcollectionmanager.data.common.UserProvider
import com.example.mtgcollectionmanager.data.common.resourceFlow
import com.example.mtgcollectionmanager.data.common.toAppError
import com.example.mtgcollectionmanager.data.common.toFirestoreId
import com.example.mtgcollectionmanager.data.local.dao.CollectionCardDao
import com.example.mtgcollectionmanager.data.local.dao.CollectionDao
import com.example.mtgcollectionmanager.data.mapper.toDomain
import com.example.mtgcollectionmanager.data.model.local.CollectionEntity
import com.example.mtgcollectionmanager.data.remote.firebase.FirestoreDataSource
import com.example.mtgcollectionmanager.data.remote.firebase.dto.FirestoreCollectionDto
import com.example.mtgcollectionmanager.data.remote.util.NetworkConnectivityManager
import com.example.mtgcollectionmanager.data.sync.CollectionSyncManager
import com.example.mtgcollectionmanager.domain.common.AppError
import com.example.mtgcollectionmanager.domain.common.Resource
import com.example.mtgcollectionmanager.domain.model.Collection
import com.example.mtgcollectionmanager.domain.repository.UserCollectionsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserCollectionsRepositoryImpl @Inject constructor(
    private val collectionDao: CollectionDao,
    private val collectionCardDao: CollectionCardDao,
    private val firestoreDataSource: FirestoreDataSource,
    private val syncManager: CollectionSyncManager,
    private val userProvider: UserProvider,
    networkConnectivityManager: NetworkConnectivityManager
) : BaseRepository(networkConnectivityManager), UserCollectionsRepository {

    private val userId: String
        get() = userProvider.getCurrentUserId()

    override suspend fun getAllCollections(): Flow<Resource<List<Collection>>> =
        executeNetworkOperationWithFallback(
            networkOperation = { fetchFromNetwork() },
            fallbackOperation = { fetchFromLocal() }
        )
    
    private fun fetchFromNetwork(): Flow<Resource<List<Collection>>> = resourceFlow {
        val remoteCollections = syncManager.syncCollections(userId)
        val collections = remoteCollections.map { dto ->
            val stats = syncManager.calculateCollectionStats(userId, dto.id)
            val entity = dto.toEntity(userId)
            entity.toDomain(stats.totalCards, stats.totalValue)
        }
        emit(Resource.Success(collections))
    }
    
    private fun fetchFromLocal(): Flow<Resource<List<Collection>>> = resourceFlow {
        try {
            collectionDao.getAllCollections(userId).collect { entities ->
                val collections = entities.map { entity ->
                    entity.toDomainWithStats()
                }
                emit(Resource.Success(collections))
            }
        } catch (_: Exception) {
            emit(Resource.Error(AppError.Collection.LoadFailed))
        }
    }

    override suspend fun getCollectionById(collectionId: Long): Flow<Resource<Collection?>> =
        executeNetworkOperationWithFallback(
            networkOperation = { fetchCollectionByIdFromNetwork(collectionId) },
            fallbackOperation = { fetchCollectionByIdFromLocal(collectionId) }
        )
        
    private fun fetchCollectionByIdFromNetwork(collectionId: Long): Flow<Resource<Collection?>> = resourceFlow {
        try {
            val firestoreId = getFirestoreId(collectionId)
            val remoteCollection = syncManager.syncCollection(userId, firestoreId)
            
            if (remoteCollection != null) {
                val stats = syncManager.calculateCollectionStats(userId, remoteCollection.id)
                val entity = remoteCollection.toEntity(userId)
                val collection = entity.toDomain(stats.totalCards, stats.totalValue)
                emit(Resource.Success(collection))
            } else {
                emit(Resource.Success(null))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.toAppError(AppError.Collection.LoadFailed)))
        }
    }
    
    private fun fetchCollectionByIdFromLocal(collectionId: Long): Flow<Resource<Collection?>> = resourceFlow {
        try {
            val localEntity = collectionDao.getCollectionById(collectionId, userId)
            val collection = localEntity?.toDomainWithStats()
            emit(Resource.Success(collection))
        } catch (_: Exception) {
            emit(Resource.Error(AppError.Collection.LoadFailed))
        }
    }

    override suspend fun createCollection(
        name: String,
        description: String
    ): Flow<Resource<Long>> = resourceFlow {
        try {
            val dto = FirestoreCollectionDto(
                name = name,
                description = description,
                createdAt = System.currentTimeMillis()
            )

            val firestoreId = firestoreDataSource.createCollection(userId, dto)

            val entity = createCollectionEntity(firestoreId, name, description, dto.createdAt)
            val localId = collectionDao.insertCollection(entity)

            emit(Resource.Success(localId))
        } catch (e: Exception) {
            emit(Resource.Error(e.toAppError(AppError.Collection.CreateFailed)))
        }
    }

    override suspend fun updateCollection(collection: Collection): Flow<Resource<Unit>> = resourceFlow {
        try {
            val firestoreId = getFirestoreIdFromCollection(collection)

            firestoreDataSource.updateCollection(
                userId,
                firestoreId,
                mapOf(
                    "name" to collection.name,
                    "description" to collection.description
                )
            )

            val entity = CollectionEntity(
                id = collection.id,
                name = collection.name,
                description = collection.description,
                createdDate = collection.createdDate,
                userId = userId,
                firestoreId = collection.firestoreId
            )
            collectionDao.updateCollection(entity)
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.toAppError(AppError.Collection.UpdateFailed)))
        }
    }

    override suspend fun deleteCollection(collectionId: Long): Flow<Resource<Unit>> = resourceFlow {
        try {
            val firestoreId = getFirestoreId(collectionId)

            firestoreDataSource.deleteCollection(userId, firestoreId)

            collectionDao.deleteCollectionById(collectionId, userId)

            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.toAppError(AppError.Collection.DeleteFailed)))
        }
    }

    override suspend fun getCollectionCount(): Int = collectionDao.getCollectionCount(userId)

    private suspend fun CollectionEntity.toDomainWithStats(): Collection {
        return try {
            val totalCards = collectionCardDao.getTotalCardCount(id, userId)
            val totalValue = collectionCardDao.getTotalValue(id, userId)
            toDomain(totalCards, totalValue)
        } catch (_: Exception) {
            toDomain(0, 0.0)
        }
    }

    private suspend fun getFirestoreId(collectionId: Long): String {
        val entity = collectionDao.getCollectionById(collectionId, userId)
        return entity?.firestoreId.toFirestoreId(collectionId)
    }

    private fun getFirestoreIdFromCollection(collection: Collection): String {
        return collection.firestoreId.toFirestoreId(collection.id)
    }

    private fun createCollectionEntity(
        firestoreId: String,
        name: String,
        description: String,
        createdAt: Long
    ): CollectionEntity {
        val id = firestoreId.hashCode().toLong().let { if (it < 0) -it else it }
        return CollectionEntity(
            id = id,
            name = name,
            description = description,
            createdDate = createdAt,
            userId = userId,
            firestoreId = firestoreId
        )
    }
}