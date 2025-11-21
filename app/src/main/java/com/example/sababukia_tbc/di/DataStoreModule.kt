package com.example.sababukia_tbc.di

import android.content.Context
import com.example.sababukia_tbc.data.DatastoreManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

    @Provides
    @Singleton
    fun provideDatastoreManager(@ApplicationContext context: Context): DatastoreManager {
        return DatastoreManager(context)
    }
}
