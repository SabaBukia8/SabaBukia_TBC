package com.example.mtgcollectionmanager.data.repository

import com.example.mtgcollectionmanager.data.common.UserProvider
import com.example.mtgcollectionmanager.data.local.dao.CategoryDao
import com.example.mtgcollectionmanager.data.local.dao.CollectionCardDao
import com.example.mtgcollectionmanager.data.mapper.toDomain
import com.example.mtgcollectionmanager.data.mapper.toEntity
import com.example.mtgcollectionmanager.data.model.local.CategoryEntity
import com.example.mtgcollectionmanager.data.remote.firebase.FirestoreDataSource
import com.example.mtgcollectionmanager.data.remote.firebase.dto.FirestoreCategoryDto
import com.example.mtgcollectionmanager.data.remote.util.NetworkConnectivityManager
import com.example.mtgcollectionmanager.domain.common.Resource
import com.example.mtgcollectionmanager.domain.model.Category
import com.example.mtgcollectionmanager.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryRepositoryImpl @Inject constructor(
    private val categoryDao: CategoryDao,
    private val collectionCardDao: CollectionCardDao,
    private val firestoreDataSource: FirestoreDataSource,
    private val userProvider: UserProvider,
    networkConnectivityManager: NetworkConnectivityManager
) : NetworkAwareRepositoryImpl(networkConnectivityManager), CategoryRepository {

    private val userId: String
        get() = userProvider.getCurrentUserId()

    override suspend fun getCategoriesByCollection(collectionId: Long): Flow<Resource<List<Category>>> =
        executeNetworkOperationWithFallback(
            networkOperation = {
                flow {
                    emit(Resource.Loading(true))
                    try {
                        val firestoreCategories =
                            firestoreDataSource.getCategoriesOnce(userId, collectionId.toString())

                        categoryDao.deleteAllCategoriesForCollection(collectionId)

                        firestoreCategories.forEach { dto ->
                            val entity = dto.toEntity(collectionId)
                            categoryDao.insertCategory(entity)
                        }

                        val categories = firestoreCategories.map { dto ->
                            val entity = dto.toEntity(collectionId)
                            val cardCount = categoryDao.getCardCountByCategory(entity.id, userId)
                            entity.toDomain(cardCount = cardCount)
                        }
                        emit(Resource.Success(categories))
                        emit(Resource.Loading(false))
                    } catch (e: Exception) {
                        emit(Resource.Error(e.message ?: "Failed to load categories from network"))
                        emit(Resource.Loading(false))
                    }
                }
            },
            fallbackOperation = {
                flow {
                    emit(Resource.Loading(true))
                    try {
                        categoryDao.getCategoriesByCollection(collectionId).collect { entities ->
                            val categories = entities.map { entity ->
                                val cardCount =
                                    categoryDao.getCardCountByCategory(entity.id, userId)
                                entity.toDomain(cardCount = cardCount)
                            }
                            emit(Resource.Success(categories))
                        }
                        emit(Resource.Loading(false))
                    } catch (cacheError: Exception) {
                        emit(
                            Resource.Error(
                                cacheError.message ?: "Failed to load categories from cache"
                            )
                        )
                        emit(Resource.Loading(false))
                    }
                }
            }
        )

    override suspend fun getCategoryById(categoryId: Long): Flow<Resource<Category?>> =
        executeNetworkOperationWithFallback(
            networkOperation = {
                flow {
                    emit(Resource.Loading(true))
                    try {
                        val entity = categoryDao.getCategoryById(categoryId)
                        if (entity != null) {
                            emit(Resource.Success(entity.toDomain()))
                        } else {
                            emit(Resource.Success(null))
                        }
                        emit(Resource.Loading(false))
                    } catch (e: Exception) {
                        emit(Resource.Error(e.message ?: "Failed to load category from network"))
                        emit(Resource.Loading(false))
                    }
                }
            },
            fallbackOperation = {
                flow {
                    emit(Resource.Loading(true))
                    try {
                        val entity = categoryDao.getCategoryById(categoryId)
                        if (entity != null) {
                            emit(Resource.Success(entity.toDomain()))
                        } else {
                            emit(Resource.Success(null))
                        }
                        emit(Resource.Loading(false))
                    } catch (e: Exception) {
                        emit(Resource.Error(e.message ?: "Failed to load category from cache"))
                        emit(Resource.Loading(false))
                    }
                }
            }
        )

    override suspend fun createCategory(
        collectionId: Long,
        name: String,
        color: String
    ): Flow<Resource<Long>> = flow {
        emit(Resource.Loading(true))
        try {
            val dto = FirestoreCategoryDto(
                name = name,
                color = color,
                createdDate = System.currentTimeMillis()
            )

            val firestoreId =
                firestoreDataSource.createCategory(userId, collectionId.toString(), dto)

            val entity = CategoryEntity(
                id = firestoreId.hashCode().toLong().let { if (it < 0) -it else it },
                collectionId = collectionId,
                name = name,
                color = color,
                createdDate = dto.createdDate
            )
            val id = categoryDao.insertCategory(entity)
            emit(Resource.Success(id))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to create category"))
        } finally {
            emit(Resource.Loading(false))
        }
    }

    override suspend fun updateCategory(category: Category): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading(true))
        try {
            firestoreDataSource.updateCategory(
                userId,
                category.collectionId.toString(),
                category.id.toString(),
                mapOf(
                    "name" to category.name,
                    "color" to category.color
                )
            )

            categoryDao.updateCategory(category.toEntity())
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to update category"))
        } finally {
            emit(Resource.Loading(false))
        }
    }

    override suspend fun deleteCategory(categoryId: Long): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading(true))
        try {
            val category = categoryDao.getCategoryById(categoryId)

            if (category != null) {
                firestoreDataSource.deleteCategory(
                    userId,
                    category.collectionId.toString(),
                    categoryId.toString()
                )
            }

            categoryDao.deleteCategoryById(categoryId)
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to delete category"))
        } finally {
            emit(Resource.Loading(false))
        }
    }

    override suspend fun moveCardToCategory(
        collectionId: Long,
        scryfallCardId: String,
        categoryId: Long?
    ): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading(true))
        try {
            val firestoreCard =
                firestoreDataSource.getCardByCardId(userId, collectionId.toString(), scryfallCardId)
            if (firestoreCard != null) {
                firestoreDataSource.updateCard(
                    userId,
                    collectionId.toString(),
                    firestoreCard.id,
                    mapOf("categoryId" to categoryId?.toString())
                )
            }

            val cardEntity =
                collectionCardDao.getCardInCollection(scryfallCardId, collectionId, userId)
            if (cardEntity != null) {
                collectionCardDao.moveCardToCategory(cardEntity.id, categoryId, userId)
                emit(Resource.Success(Unit))
            } else {
                emit(Resource.Error("Card not found in collection"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to move card to category"))
        } finally {
            emit(Resource.Loading(false))
        }
    }
}
