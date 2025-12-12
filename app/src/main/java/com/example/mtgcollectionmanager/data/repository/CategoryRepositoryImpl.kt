package com.example.mtgcollectionmanager.data.repository

import com.example.mtgcollectionmanager.data.local.dao.CategoryDao
import com.example.mtgcollectionmanager.data.local.dao.CollectionCardDao
import com.example.mtgcollectionmanager.data.mapper.toDomain
import com.example.mtgcollectionmanager.data.mapper.toEntity
import com.example.mtgcollectionmanager.data.model.local.CategoryEntity
import com.example.mtgcollectionmanager.domain.common.Resource
import com.example.mtgcollectionmanager.domain.model.Category
import com.example.mtgcollectionmanager.domain.repository.AuthRepository
import com.example.mtgcollectionmanager.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryRepositoryImpl @Inject constructor(
    private val categoryDao: CategoryDao,
    private val collectionCardDao: CollectionCardDao,
    private val authRepository: AuthRepository
) : CategoryRepository {

    private val userId: String
        get() = authRepository.getCurrentUser()?.uid ?: ""

    override fun getCategoriesByCollection(collectionId: Long): Flow<Resource<List<Category>>> = flow {
        emit(Resource.Loading(true))
        emit(Resource.Loading(false))
        try {
            categoryDao.getCategoriesByCollection(collectionId).collect { entities ->
                // Get card count for each category
                val categories = entities.map { entity ->
                    val cardCount = collectionCardDao.getCardsByCategory(
                        collectionId = entity.collectionId,
                        categoryId = entity.id,
                        userId = userId
                    ).map { it.size }

                    // For now, we'll set cardCount to 0 since we'd need to collect the flow
                    // This can be improved with a dedicated query in the DAO
                    entity.toDomain(cardCount = 0)
                }
                emit(Resource.Success(categories))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to load categories"))
        }
    }

    override fun getCategoryById(categoryId: Long): Flow<Resource<Category?>> = flow {
        emit(Resource.Loading(true))
        emit(Resource.Loading(false))
        try {
            val entity = categoryDao.getCategoryById(categoryId)
            if (entity != null) {
                emit(Resource.Success(entity.toDomain()))
            } else {
                emit(Resource.Success(null))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to load category"))
        }
    }

    override suspend fun createCategory(
        collectionId: Long,
        name: String,
        color: String
    ): Flow<Resource<Long>> = flow {
        emit(Resource.Loading(true))
        try {
            val entity = CategoryEntity(
                collectionId = collectionId,
                name = name,
                color = color,
                createdDate = System.currentTimeMillis()
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
            categoryDao.deleteCategoryById(categoryId)
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to delete category"))
        } finally {
            emit(Resource.Loading(false))
        }
    }

    override suspend fun moveCardToCategory(cardId: Long, categoryId: Long?): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading(true))
        try {
            collectionCardDao.moveCardToCategory(cardId, categoryId, userId)
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to move card to category"))
        } finally {
            emit(Resource.Loading(false))
        }
    }
}
