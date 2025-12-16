package com.example.mtgcollectionmanager.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.mtgcollectionmanager.data.model.local.CollectionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CollectionDao {
    @Query(
        """
        SELECT DISTINCT collections.* FROM collections
        LEFT JOIN collection_cards ON collections.id = collection_cards.collectionId
        WHERE collections.userId = :userId
        ORDER BY collections.createdDate DESC
    """
    )
    fun getAllCollections(userId: String): Flow<List<CollectionEntity>>

    @Query("SELECT * FROM collections WHERE id = :collectionId AND userId = :userId")
    suspend fun getCollectionById(collectionId: Long, userId: String): CollectionEntity?

    @Query(
        """
        SELECT DISTINCT collections.* FROM collections
        LEFT JOIN collection_cards ON collections.id = collection_cards.collectionId
        WHERE collections.id = :collectionId AND collections.userId = :userId
    """
    )
    fun getCollectionByIdFlow(collectionId: Long, userId: String): Flow<CollectionEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCollection(collection: CollectionEntity): Long

    @Update
    suspend fun updateCollection(collection: CollectionEntity)

    @Delete
    suspend fun deleteCollection(collection: CollectionEntity)

    @Query("DELETE FROM collections WHERE id = :collectionId AND userId = :userId")
    suspend fun deleteCollectionById(collectionId: Long, userId: String)

    @Query("SELECT COUNT(*) FROM collections WHERE userId = :userId")
    suspend fun getCollectionCount(userId: String): Int

    @Query("DELETE FROM collections WHERE userId = :userId")
    suspend fun deleteAllCollectionsForUser(userId: String)
}
