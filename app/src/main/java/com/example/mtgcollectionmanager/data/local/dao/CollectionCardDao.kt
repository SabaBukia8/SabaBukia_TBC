package com.example.mtgcollectionmanager.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.mtgcollectionmanager.data.model.local.CollectionCardEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CollectionCardDao {
    // Get all cards in a collection
    @Query("SELECT * FROM collection_cards WHERE collectionId = :collectionId AND userId = :userId ORDER BY addedDate DESC")
    fun getCardsByCollection(collectionId: Long, userId: String): Flow<List<CollectionCardEntity>>

    // Get cards in a specific category
    @Query("SELECT * FROM collection_cards WHERE collectionId = :collectionId AND categoryId = :categoryId AND userId = :userId ORDER BY addedDate DESC")
    fun getCardsByCategory(collectionId: Long, categoryId: Long, userId: String): Flow<List<CollectionCardEntity>>

    // Get uncategorized cards in a collection
    @Query("SELECT * FROM collection_cards WHERE collectionId = :collectionId AND categoryId IS NULL AND userId = :userId ORDER BY addedDate DESC")
    fun getUncategorizedCards(collectionId: Long, userId: String): Flow<List<CollectionCardEntity>>

    // Get cards by color in a collection
    @Query("SELECT * FROM collection_cards WHERE collectionId = :collectionId AND userId = :userId AND colorsJson LIKE :color ORDER BY addedDate DESC")
    fun getCardsByColor(collectionId: Long, userId: String, color: String): Flow<List<CollectionCardEntity>>

    // Get cards by set in a collection
    @Query("SELECT * FROM collection_cards WHERE collectionId = :collectionId AND userId = :userId AND setCode = :setCode ORDER BY addedDate DESC")
    fun getCardsBySet(collectionId: Long, userId: String, setCode: String): Flow<List<CollectionCardEntity>>

    // Get a specific card by its internal ID
    @Query("SELECT * FROM collection_cards WHERE id = :id AND userId = :userId")
    suspend fun getCardByInternalId(id: Long, userId: String): CollectionCardEntity?

    // Check if a card exists in a collection
    @Query("SELECT * FROM collection_cards WHERE cardId = :cardId AND collectionId = :collectionId AND userId = :userId LIMIT 1")
    suspend fun getCardInCollection(cardId: String, collectionId: Long, userId: String): CollectionCardEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCard(card: CollectionCardEntity): Long

    @Update
    suspend fun updateCard(card: CollectionCardEntity)

    @Delete
    suspend fun deleteCard(card: CollectionCardEntity)

    @Query("DELETE FROM collection_cards WHERE id = :id AND userId = :userId")
    suspend fun deleteCardByInternalId(id: Long, userId: String)

    // Move card to a different category
    @Query("UPDATE collection_cards SET categoryId = :categoryId WHERE id = :cardId AND userId = :userId")
    suspend fun moveCardToCategory(cardId: Long, categoryId: Long?, userId: String)

    // Get total value of a collection
    @Query("SELECT SUM(price * quantity) FROM collection_cards WHERE collectionId = :collectionId AND userId = :userId")
    suspend fun getTotalValue(collectionId: Long, userId: String): Double?

    // Get total card count in a collection
    @Query("SELECT SUM(quantity) FROM collection_cards WHERE collectionId = :collectionId AND userId = :userId")
    suspend fun getTotalCardCount(collectionId: Long, userId: String): Int?

    @Query("DELETE FROM collection_cards WHERE userId = :userId")
    suspend fun deleteAllCards(userId: String)

    @Query("SELECT SUM(quantity) FROM collection_cards WHERE userId = :userId")
    suspend fun getTotalCardCountForUser(userId: String): Int?

    @Query("DELETE FROM collection_cards WHERE collectionId = :collectionId AND userId = :userId")
    suspend fun deleteAllCardsForCollection(collectionId: Long, userId: String)
}
