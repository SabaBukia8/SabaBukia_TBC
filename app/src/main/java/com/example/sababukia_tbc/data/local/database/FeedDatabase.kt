package com.example.sababukia_tbc.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.sababukia_tbc.data.local.dao.PostDao
import com.example.sababukia_tbc.data.local.dao.StoryDao
import com.example.sababukia_tbc.data.model.local.PostEntity
import com.example.sababukia_tbc.data.model.local.StoryEntity

@Database(
    entities = [StoryEntity::class, PostEntity::class],
    version = 1,
    exportSchema = false
)
abstract class FeedDatabase : RoomDatabase() {
    abstract fun storyDao(): StoryDao
    abstract fun postDao(): PostDao
}
