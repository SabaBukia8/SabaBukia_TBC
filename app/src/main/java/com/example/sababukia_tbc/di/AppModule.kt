package com.example.sababukia_tbc.di

import com.example.sababukia_tbc.data.repository.ImageRepositoryImpl
import com.example.sababukia_tbc.data.repository.StorageRepositoryImpl
import com.example.sababukia_tbc.domain.repository.ImageRepository
import com.example.sababukia_tbc.domain.repository.StorageRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Suppress("unused")
@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    @Singleton
    abstract fun bindImageRepository(
        imageRepositoryImpl: ImageRepositoryImpl
    ): ImageRepository

    @Binds
    @Singleton
    abstract fun bindStorageRepository(
        storageRepositoryImpl: StorageRepositoryImpl
    ): StorageRepository
}
