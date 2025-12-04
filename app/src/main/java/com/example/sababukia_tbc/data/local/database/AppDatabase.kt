package com.example.sababukia_tbc.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.sababukia_tbc.data.local.dao.UserDao
import com.example.sababukia_tbc.data.model.local.UserEntity

@Database(
    entities = [UserEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}
