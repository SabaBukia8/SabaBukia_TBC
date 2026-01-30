package com.example.sababukia_tbc.di

import com.example.sababukia_tbc.data.remote.api.FeedApiService
import com.example.sababukia_tbc.data.repository.FeedRepositoryImpl
import com.example.sababukia_tbc.domain.repository.FeedRepository
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
    fun provideFeedApiService(retrofit: Retrofit): FeedApiService {
        return retrofit.create(FeedApiService::class.java)
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class FeedBindingsModule {

    @Binds
    @Singleton
    abstract fun bindFeedRepository(impl: FeedRepositoryImpl): FeedRepository
}
