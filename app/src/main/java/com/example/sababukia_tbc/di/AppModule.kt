package com.example.sababukia_tbc.di

import com.example.sababukia_tbc.data.local.ILocalDataSource
import com.example.sababukia_tbc.data.local.datasource.LocalDataSourceImpl
import com.example.sababukia_tbc.data.remote.AuthRemoteDataSourceImpl
import com.example.sababukia_tbc.data.remote.IAuthRemoteDataSource
import com.example.sababukia_tbc.data.repository.AuthRepositoryImpl
import com.example.sababukia_tbc.domain.repository.IAuthRepository
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
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): IAuthRepository

    @Binds
    @Singleton
    abstract fun bindAuthRemoteDataSource(
        authRemoteDataSourceImpl: AuthRemoteDataSourceImpl
    ): IAuthRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindLocalDataSource(
        localDataSourceImpl: LocalDataSourceImpl
    ): ILocalDataSource
}
