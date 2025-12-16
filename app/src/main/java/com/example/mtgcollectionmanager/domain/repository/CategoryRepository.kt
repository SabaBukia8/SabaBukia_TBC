package com.example.mtgcollectionmanager.domain.repository

import com.example.mtgcollectionmanager.domain.common.Resource
import com.example.mtgcollectionmanager.domain.model.Category
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
    suspend fun getCategoriesByCollection(collectionId: Long): Flow<Resource<List<Category>>>
    suspend fun getCategoryById(categoryId: Long): Flow<Resource<Category?>>
    suspend fun createCategory(
        collectionId: Long,
        name: String,
        color: String
    ): Flow<Resource<Long>>

    suspend fun updateCategory(category: Category): Flow<Resource<Unit>>
    suspend fun deleteCategory(categoryId: Long): Flow<Resource<Unit>>
    suspend fun moveCardToCategory(
        collectionId: Long,
        scryfallCardId: String,
        categoryId: Long?
    ): Flow<Resource<Unit>>
}
