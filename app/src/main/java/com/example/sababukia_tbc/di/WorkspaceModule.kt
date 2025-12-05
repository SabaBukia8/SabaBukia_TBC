package com.example.sababukia_tbc.di

import com.example.sababukia_tbc.data.remote.api.WorkspaceApiService
import com.example.sababukia_tbc.data.repository.WorkspaceRepositoryImpl
import com.example.sababukia_tbc.domain.repository.WorkspaceRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object WorkspaceModule {

    @Provides
    @Singleton
    fun provideWorkspaceApiService(@AppRetrofit retrofit: Retrofit): WorkspaceApiService =
        retrofit.create(WorkspaceApiService::class.java)
}

@Module
@InstallIn(SingletonComponent::class)
interface WorkspaceBindsModule {

    @Binds
    @Singleton
    fun bindWorkspaceRepository(impl: WorkspaceRepositoryImpl): WorkspaceRepository
}
