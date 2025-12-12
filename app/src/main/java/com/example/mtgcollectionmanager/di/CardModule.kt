package com.example.mtgcollectionmanager.di

import com.example.mtgcollectionmanager.data.remote.api.ScryfallApiService
import com.example.mtgcollectionmanager.data.repository.CardRepositoryImpl
import com.example.mtgcollectionmanager.domain.repository.CardRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CardModule {

    @Provides
    @Singleton
    fun provideScryfallApiService(retrofit: Retrofit): ScryfallApiService =
        retrofit.create(ScryfallApiService::class.java)
}

@Module
@InstallIn(SingletonComponent::class)
interface CardBindsModule {

    @Binds
    @Singleton
    fun bindCardRepository(impl: CardRepositoryImpl): CardRepository
}
