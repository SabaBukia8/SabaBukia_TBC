package com.example.mtgcollectionmanager.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.mtgcollectionmanager.data.model.local.CategoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Query("SELECT * FROM categories WHERE collectionId = :collectionId ORDER BY createdDate ASC")
    fun getCategoriesByCollection(collectionId: Long): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM categories WHERE id = :categoryId")
    suspend fun getCategoryById(categoryId: Long): CategoryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: CategoryEntity): Long

    @Update
    suspend fun updateCategory(category: CategoryEntity)

    @Delete
    suspend fun deleteCategory(category: CategoryEntity)

    @Query("DELETE FROM categories WHERE id = :categoryId")
    suspend fun deleteCategoryById(categoryId: Long)

    @Query("SELECT COUNT(*) FROM categories WHERE collectionId = :collectionId")
    suspend fun getCategoryCount(collectionId: Long): Int

    @Query("SELECT COUNT(*) FROM collection_cards WHERE categoryId = :categoryId AND userId = :userId")
    suspend fun getCardCountByCategory(categoryId: Long, userId: String): Int

    @Query("DELETE FROM categories WHERE collectionId = :collectionId")
    suspend fun deleteAllCategoriesForCollection(collectionId: Long)
}
