package com.example.sababukia_tbc.di

import android.content.Context
import com.example.sababukia_tbc.data.DatastoreManager
import com.example.sababukia_tbc.data.local.ILocalDataSource
import com.example.sababukia_tbc.data.local.datasource.LocalDataSourceImpl
import com.example.sababukia_tbc.data.remote.IRemoteDataSource
import com.example.sababukia_tbc.data.remote.RemoteDataSourceImpl
import com.example.sababukia_tbc.data.repository.AuthRepositoryImpl
import com.example.sababukia_tbc.domain.repository.IAuthRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


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
    abstract fun bindRemoteDataSource(
        remoteDataSourceImpl: RemoteDataSourceImpl
    ): IRemoteDataSource


    @Binds
    @Singleton
    abstract fun bindLocalDataSource(
        localDataSourceImpl: LocalDataSourceImpl
    ): ILocalDataSource

    companion object {

        @Provides
        @Singleton
        fun provideContext(@ApplicationContext context: Context): Context {
            return context
        }


        @Provides
        @Singleton
        fun provideDatastoreManager(@ApplicationContext context: Context): DatastoreManager {
            return DatastoreManager(context)
        }
    }
}
