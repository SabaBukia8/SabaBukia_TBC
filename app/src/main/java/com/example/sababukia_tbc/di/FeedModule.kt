package com.example.sababukia_tbc.di

import com.example.sababukia_tbc.data.remote.api.FeedApiService
import com.example.sababukia_tbc.data.repository.PostRepositoryImpl
import com.example.sababukia_tbc.data.repository.StoryRepositoryImpl
import com.example.sababukia_tbc.domain.repository.PostRepository
import com.example.sababukia_tbc.domain.repository.StoryRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FeedModule {

    @Provides
    @Singleton
    fun provideFeedApiService(@AppRetrofit retrofit: Retrofit): FeedApiService =
        retrofit.create(FeedApiService::class.java)
}

@Module
@InstallIn(SingletonComponent::class)
interface FeedBindsModule {

    @Binds
    @Singleton
    fun bindStoryRepository(impl: StoryRepositoryImpl): StoryRepository

    @Binds
    @Singleton
    fun bindPostRepository(impl: PostRepositoryImpl): PostRepository
}
