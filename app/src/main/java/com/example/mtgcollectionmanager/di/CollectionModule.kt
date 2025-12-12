package com.example.mtgcollectionmanager.di

import com.example.mtgcollectionmanager.data.repository.CategoryRepositoryImpl
import com.example.mtgcollectionmanager.data.repository.CollectionRepositoryImpl
import com.example.mtgcollectionmanager.data.repository.UserCollectionsRepositoryImpl
import com.example.mtgcollectionmanager.domain.repository.CategoryRepository
import com.example.mtgcollectionmanager.domain.repository.CollectionRepository
import com.example.mtgcollectionmanager.domain.repository.UserCollectionsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface CollectionModule {

    @Binds
    @Singleton
    fun bindCollectionRepository(impl: CollectionRepositoryImpl): CollectionRepository

    @Binds
    @Singleton
    fun bindUserCollectionsRepository(impl: UserCollectionsRepositoryImpl): UserCollectionsRepository

    @Binds
    @Singleton
    fun bindCategoryRepository(impl: CategoryRepositoryImpl): CategoryRepository
}
