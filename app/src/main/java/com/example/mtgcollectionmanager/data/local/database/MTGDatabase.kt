package com.example.mtgcollectionmanager.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.mtgcollectionmanager.data.local.dao.CategoryDao
import com.example.mtgcollectionmanager.data.local.dao.CollectionCardDao
import com.example.mtgcollectionmanager.data.local.dao.CollectionDao
import com.example.mtgcollectionmanager.data.model.local.CategoryEntity
import com.example.mtgcollectionmanager.data.model.local.CollectionCardEntity
import com.example.mtgcollectionmanager.data.model.local.CollectionEntity

@Database(
    entities = [
        CollectionEntity::class,
        CategoryEntity::class,
        CollectionCardEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class MTGDatabase : RoomDatabase() {
    abstract fun collectionDao(): CollectionDao
    abstract fun categoryDao(): CategoryDao
    abstract fun collectionCardDao(): CollectionCardDao
}
