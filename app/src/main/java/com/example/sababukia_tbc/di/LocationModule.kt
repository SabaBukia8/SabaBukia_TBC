package com.example.sababukia_tbc.di

import android.content.Context
import androidx.room.Room
import com.example.sababukia_tbc.data.local.LocalDataSource
import com.example.sababukia_tbc.data.local.LocationDao
import com.example.sababukia_tbc.data.local.LocationDatabase
import com.example.sababukia_tbc.data.remote.RemoteDataSource
import com.example.sababukia_tbc.data.remote.api.LocationApiService
import com.example.sababukia_tbc.data.repository.LocationRepositoryImpl
import com.example.sababukia_tbc.domain.repository.LocationRepository
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocationModule {

    @Provides
    @Singleton
    fun provideFusedLocationProviderClient(
        @ApplicationContext context: Context
    ): FusedLocationProviderClient {
        return LocationServices.getFusedLocationProviderClient(context)
    }

    @Provides
    @Singleton
    fun provideLocationDatabase(
        @ApplicationContext context: Context
    ): LocationDatabase {
        return Room.databaseBuilder(
            context,
            LocationDatabase::class.java,
            "locations_db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideLocationDao(database: LocationDatabase): LocationDao {
        return database.locationDao()
    }

    @Provides
    @Singleton
    fun provideLocalDataSource(locationDao: LocationDao): LocalDataSource {
        return LocalDataSource(locationDao)
    }

    @Provides
    @Singleton
    fun provideRemoteDataSource(apiService: LocationApiService): RemoteDataSource {
        return RemoteDataSource(apiService)
    }

    @Provides
    @Singleton
    fun provideLocationRepository(
        @ApplicationContext context: Context,
        fusedLocationClient: FusedLocationProviderClient,
        localDataSource: LocalDataSource,
        remoteDataSource: RemoteDataSource
    ): LocationRepository {
        return LocationRepositoryImpl(
            context,
            fusedLocationClient,
            localDataSource,
            remoteDataSource)
    }
}