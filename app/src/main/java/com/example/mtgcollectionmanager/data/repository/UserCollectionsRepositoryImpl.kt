package com.example.mtgcollectionmanager.data.repository

import com.example.mtgcollectionmanager.data.common.UserProvider
import com.example.mtgcollectionmanager.data.local.dao.CollectionCardDao
import com.example.mtgcollectionmanager.data.local.dao.CollectionDao
import com.example.mtgcollectionmanager.data.mapper.toDomain
import com.example.mtgcollectionmanager.data.mapper.toEntity
import com.example.mtgcollectionmanager.data.model.local.CollectionEntity
import com.example.mtgcollectionmanager.data.remote.firebase.FirestoreDataSource
import com.example.mtgcollectionmanager.data.remote.firebase.dto.FirestoreCollectionDto
import com.example.mtgcollectionmanager.data.remote.util.NetworkConnectivityManager
import com.example.mtgcollectionmanager.domain.common.Resource
import com.example.mtgcollectionmanager.domain.model.Collection
import com.example.mtgcollectionmanager.domain.repository.UserCollectionsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserCollectionsRepositoryImpl @Inject constructor(
    private val collectionDao: CollectionDao,
    private val collectionCardDao: CollectionCardDao,
    private val firestoreDataSource: FirestoreDataSource,
    private val userProvider: UserProvider,
    networkConnectivityManager: NetworkConnectivityManager
) : NetworkAwareRepositoryImpl(networkConnectivityManager), UserCollectionsRepository {

    private val userId: String
        get() = userProvider.getCurrentUserId()

    override suspend fun getAllCollections(): Flow<Resource<List<Collection>>> =
        executeNetworkOperationWithFallback(
            networkOperation = {
                flow {
                    emit(Resource.Loading(true))
                    try {
                        val firestoreCollections = firestoreDataSource.getCollectionsOnce(userId)

                        // Only delete collections and cards if we have data from Firestore
                        if (firestoreCollections.isNotEmpty()) {
                            collectionDao.deleteAllCollectionsForUser(userId)
                            collectionCardDao.deleteAllCards(userId)
                        }

                        firestoreCollections.forEach { dto ->
                            val entity = dto.toEntity(userId)
                            collectionDao.insertCollection(entity)
                        }

                        val collections = firestoreCollections.map { collectionDto ->
                            val entity = collectionDto.toEntity(userId)


                            val cards = firestoreDataSource.getCardsOnce(userId, collectionDto.id)


                            val totalCards = cards.sumOf { it.quantity }
                            val totalValue = cards.sumOf { it.price * it.quantity }

                            entity.toDomain(totalCards, totalValue)
                        }
                        emit(Resource.Success(collections))
                        emit(Resource.Loading(false))
                    } catch (e: Exception) {
                        emit(Resource.Error(e.message ?: "Failed to load collections from network"))
                        emit(Resource.Loading(false))
                    }
                }
            },
            fallbackOperation = {
                flow {
                    emit(Resource.Loading(true))
                    try {
                        collectionDao.getAllCollections(userId).collect { entities ->
                            val collections = entities.map { entity ->
                                try {

                                    val totalCards =
                                        collectionCardDao.getTotalCardCount(entity.id, userId)
                                    val totalValue =
                                        collectionCardDao.getTotalValue(entity.id, userId)
                                    entity.toDomain(totalCards, totalValue)
                                } catch (e: Exception) {
                                    entity.toDomain(0, 0.0)
                                }
                            }
                            emit(Resource.Success(collections))
                        }
                        emit(Resource.Loading(false))
                    } catch (cacheError: Exception) {
                        emit(
                            Resource.Error(
                                cacheError.message ?: "Failed to load collections from cache"
                            )
                        )
                        emit(Resource.Loading(false))
                    }
                }
            }
        )

    override suspend fun getCollectionById(collectionId: Long): Flow<Resource<Collection?>> =
        executeNetworkOperationWithFallback(
            networkOperation = {
                flow {
                    emit(Resource.Loading(true))
                    try {

                        val localEntity = collectionDao.getCollectionById(collectionId, userId)
                        val firestoreId = localEntity?.firestoreId?.takeIf { it.isNotEmpty() }
                            ?: collectionId.toString()

                        val firestoreCollection =
                            firestoreDataSource.getCollectionById(userId, firestoreId)

                        if (firestoreCollection != null) {
                            val entity = firestoreCollection.toEntity(userId)
                            collectionDao.insertCollection(entity)


                            val cards =
                                firestoreDataSource.getCardsOnce(userId, firestoreCollection.id)


                            val totalCards = cards.sumOf { it.quantity }
                            val totalValue = cards.sumOf { it.price * it.quantity }

                            emit(Resource.Success(entity.toDomain(totalCards, totalValue)))
                        } else {

                            emit(Resource.Success(null))
                        }
                        emit(Resource.Loading(false))
                    } catch (e: Exception) {
                        emit(Resource.Error(e.message ?: "Failed to load collection from network"))
                        emit(Resource.Loading(false))
                    }
                }
            },
            fallbackOperation = {
                flow {
                    emit(Resource.Loading(true))
                    try {
                        val localEntity = collectionDao.getCollectionById(collectionId, userId)
                        if (localEntity != null) {
                            val totalCards =
                                collectionCardDao.getTotalCardCount(localEntity.id, userId)
                            val totalValue = collectionCardDao.getTotalValue(localEntity.id, userId)
                            emit(Resource.Success(localEntity.toDomain(totalCards, totalValue)))
                        } else {
                            emit(Resource.Success(null))
                        }
                        emit(Resource.Loading(false))
                    } catch (cacheError: Exception) {
                        emit(
                            Resource.Error(
                                cacheError.message ?: "Failed to load collection from cache"
                            )
                        )
                        emit(Resource.Loading(false))
                    }
                }
            }
        )

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
                userId = userId,
                firestoreId = firestoreId
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
            val firestoreId = if (collection.firestoreId.isNotEmpty()) {
                collection.firestoreId
            } else {
                collection.id.toString()
            }

            firestoreDataSource.updateCollection(
                userId,
                firestoreId,
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

            val entity = collectionDao.getCollectionById(collectionId, userId)
            val firestoreId =
                entity?.firestoreId?.takeIf { it.isNotEmpty() } ?: collectionId.toString()

            firestoreDataSource.deleteCollection(userId, firestoreId)

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