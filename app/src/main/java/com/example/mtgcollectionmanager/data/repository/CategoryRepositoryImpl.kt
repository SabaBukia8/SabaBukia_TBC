package com.example.mtgcollectionmanager.data.repository

import com.example.mtgcollectionmanager.data.common.UserProvider
import com.example.mtgcollectionmanager.data.common.resourceFlow
import com.example.mtgcollectionmanager.data.common.toAppError
import com.example.mtgcollectionmanager.data.common.toPositiveLongId
import com.example.mtgcollectionmanager.data.local.dao.CategoryDao
import com.example.mtgcollectionmanager.data.local.dao.CollectionCardDao
import com.example.mtgcollectionmanager.data.mapper.toDomain
import com.example.mtgcollectionmanager.data.mapper.toEntity
import com.example.mtgcollectionmanager.data.model.local.CategoryEntity
import com.example.mtgcollectionmanager.data.remote.firebase.FirestoreDataSource
import com.example.mtgcollectionmanager.data.remote.firebase.dto.FirestoreCategoryDto
import com.example.mtgcollectionmanager.data.remote.util.NetworkConnectivityManager
import com.example.mtgcollectionmanager.domain.common.AppError
import com.example.mtgcollectionmanager.domain.common.Resource
import com.example.mtgcollectionmanager.domain.model.Category
import com.example.mtgcollectionmanager.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryRepositoryImpl @Inject constructor(
    private val categoryDao: CategoryDao,
    private val collectionCardDao: CollectionCardDao,
    private val firestoreDataSource: FirestoreDataSource,
    private val userProvider: UserProvider,
    networkConnectivityManager: NetworkConnectivityManager
) : BaseRepository(networkConnectivityManager), CategoryRepository {

    private val userId: String
        get() = userProvider.getCurrentUserId()

    override suspend fun getCategoriesByCollection(collectionId: Long): Flow<Resource<List<Category>>> =
        executeNetworkOperationWithFallback(
            networkOperation = {
                resourceFlow {
                    try {
                        val categories = fetchAndSyncCategoriesFromNetwork(collectionId)
                        emit(Resource.Success(categories))
                    } catch (e: Exception) {
                        emit(Resource.Error(e.toAppError()))
                    }
                }
            },
            fallbackOperation = {
                resourceFlow {
                    try {
                        categoryDao.getCategoriesByCollection(collectionId).collect { entities ->
                            val categories = entities.map { entity ->
                                val cardCount = categoryDao.getCardCountByCategory(entity.id, userId)
                                entity.toDomain(cardCount = cardCount)
                            }
                            emit(Resource.Success(categories))
                        }
                    } catch (_: Exception) {
                        emit(Resource.Error(AppError.Category.LoadFailed))
                    }
                }
            }
        )

    override suspend fun getCategoryById(categoryId: Long): Flow<Resource<Category?>> =
        executeNetworkOperationWithFallback(
            networkOperation = { getCategoryByIdFromLocal(categoryId) },
            fallbackOperation = { getCategoryByIdFromLocal(categoryId) }
        )

    override suspend fun createCategory(
        collectionId: Long,
        name: String,
        color: String
    ): Flow<Resource<Long>> = resourceFlow {
        try {
            val dto = FirestoreCategoryDto(
                name = name,
                color = color,
                createdDate = System.currentTimeMillis()
            )

            val firestoreId = firestoreDataSource.createCategory(
                userId,
                collectionId.toString(),
                dto
            )

            val entity = createCategoryEntity(firestoreId, collectionId, name, color, dto.createdDate)
            val id = categoryDao.insertCategory(entity)
            emit(Resource.Success(id))
        } catch (e: Exception) {
            emit(Resource.Error(e.toAppError(AppError.Category.CreateFailed)))
        }
    }

    override suspend fun updateCategory(category: Category): Flow<Resource<Unit>> = resourceFlow {
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
            emit(Resource.Error(e.toAppError(AppError.Category.UpdateFailed)))
        }
    }

    override suspend fun deleteCategory(categoryId: Long): Flow<Resource<Unit>> = resourceFlow {
        try {
            categoryDao.getCategoryById(categoryId)?.let { category ->
                firestoreDataSource.deleteCategory(
                    userId,
                    category.collectionId.toString(),
                    categoryId.toString()
                )
            }

            categoryDao.deleteCategoryById(categoryId)
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.toAppError(AppError.Category.DeleteFailed)))
        }
    }

    override suspend fun moveCardToCategory(
        collectionId: Long,
        scryfallCardId: String,
        categoryId: Long?
    ): Flow<Resource<Unit>> = resourceFlow {
        try {
            updateCardCategoryInFirestore(collectionId, scryfallCardId, categoryId)

            val cardEntity = collectionCardDao.getCardInCollection(
                scryfallCardId,
                collectionId,
                userId
            )

            if (cardEntity != null) {
                collectionCardDao.moveCardToCategory(cardEntity.id, categoryId, userId)
                emit(Resource.Success(Unit))
            } else {
                emit(Resource.Error(AppError.Card.NotFound))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.toAppError(AppError.Category.MoveCardFailed)))
        }
    }

    private suspend fun fetchAndSyncCategoriesFromNetwork(collectionId: Long): List<Category> {
        val firestoreCategories = firestoreDataSource.getCategoriesOnce(
            userId,
            collectionId.toString()
        )

        categoryDao.deleteAllCategoriesForCollection(collectionId)

        firestoreCategories.forEach { dto ->
            val entity = dto.toEntity(collectionId)
            categoryDao.insertCategory(entity)
        }

        return firestoreCategories.map { dto ->
            val entity = dto.toEntity(collectionId)
            val cardCount = categoryDao.getCardCountByCategory(entity.id, userId)
            entity.toDomain(cardCount = cardCount)
        }
    }

    private fun getCategoryByIdFromLocal(categoryId: Long): Flow<Resource<Category?>> = resourceFlow {
        try {
            val entity = categoryDao.getCategoryById(categoryId)
            emit(Resource.Success(entity?.toDomain()))
        } catch (_: Exception) {
            emit(Resource.Error(AppError.Category.LoadFailed))
        }
    }

    private suspend fun updateCardCategoryInFirestore(
        collectionId: Long,
        scryfallCardId: String,
        categoryId: Long?
    ) {
        val firestoreCard = firestoreDataSource.getCardByCardId(
            userId,
            collectionId.toString(),
            scryfallCardId
        )

        firestoreCard?.let {
            firestoreDataSource.updateCard(
                userId,
                collectionId.toString(),
                it.id,
                mapOf("categoryId" to categoryId?.toString())
            )
        }
    }

    private fun createCategoryEntity(
        firestoreId: String,
        collectionId: Long,
        name: String,
        color: String,
        createdDate: Long
    ): CategoryEntity {
        val id = firestoreId.toPositiveLongId()
        return CategoryEntity(
            id = id,
            collectionId = collectionId,
            name = name,
            color = color,
            createdDate = createdDate
        )
    }
}