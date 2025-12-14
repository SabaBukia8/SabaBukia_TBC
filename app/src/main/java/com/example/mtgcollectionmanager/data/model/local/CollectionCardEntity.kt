package com.example.mtgcollectionmanager.data.model.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "collection_cards",
    foreignKeys = [
        ForeignKey(
            entity = CollectionEntity::class,
            parentColumns = ["id"],
            childColumns = ["collectionId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("collectionId"), Index("categoryId")]
)
data class CollectionCardEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val collectionId: Long,
    val categoryId: Long?,
    val cardId: String,
    val name: String,
    val manaCost: String,
    val imageUrl: String,
    val type: String,
    val rarity: String,
    val setCode: String,
    val setName: String,
    val colorsJson: String,
    val price: Double,
    val quantity: Int,
    val condition: String,
    val addedDate: Long,
    val notes: String,
    val userId: String
)
