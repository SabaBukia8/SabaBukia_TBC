package com.example.mtgcollectionmanager.di

import android.content.Context
import androidx.room.Room
import com.example.mtgcollectionmanager.data.local.dao.CollectionCardDao
import com.example.mtgcollectionmanager.data.local.database.MTGDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideMTGDatabase(@ApplicationContext context: Context): MTGDatabase =
        Room.databaseBuilder(
            context,
            MTGDatabase::class.java,
            "mtg_database"
        )
            .fallbackToDestructiveMigration() // Start fresh with new schema
            .build()

    @Provides
    @Singleton
    fun provideCollectionDao(database: MTGDatabase) = database.collectionDao()

    @Provides
    @Singleton
    fun provideCategoryDao(database: MTGDatabase) = database.categoryDao()

    @Provides
    @Singleton
    fun provideCollectionCardDao(database: MTGDatabase) = database.collectionCardDao()
}
