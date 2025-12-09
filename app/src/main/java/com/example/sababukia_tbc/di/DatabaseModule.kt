package com.example.sababukia_tbc.di

import android.content.Context
import androidx.room.Room
import com.example.sababukia_tbc.data.local.dao.PostDao
import com.example.sababukia_tbc.data.local.dao.StoryDao
import com.example.sababukia_tbc.data.local.database.FeedDatabase
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
    fun provideFeedDatabase(@ApplicationContext context: Context): FeedDatabase =
        Room.databaseBuilder(
            context,
            FeedDatabase::class.java,
            "feed_database"
        ).build()

    @Provides
    @Singleton
    fun provideStoryDao(database: FeedDatabase): StoryDao = database.storyDao()

    @Provides
    @Singleton
    fun providePostDao(database: FeedDatabase): PostDao = database.postDao()
}
