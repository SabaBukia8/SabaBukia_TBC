package com.example.sababukia_tbc.di

import android.content.Context
import com.example.sababukia_tbc.AppPreferences
import com.example.sababukia_tbc.UserProfiles
import com.example.sababukia_tbc.data.DatastoreManager
import com.example.sababukia_tbc.data.model.local.datastore.AppPreferencesSerializer
import com.example.sababukia_tbc.data.model.local.datastore.ProtoDataStoreManager
import com.example.sababukia_tbc.data.model.local.datastore.UserProfileSerializer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AppPreferencesDataStore

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class UserProfileDataStore

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

    @Provides
    @Singleton
    @AppPreferencesDataStore
    fun provideAppPreferencesDataStore(
        @ApplicationContext context: Context
    ): ProtoDataStoreManager<AppPreferences> {
        return ProtoDataStoreManager.create(
            context = context,
            fileName = "app_preferences.pb",
            serializer = AppPreferencesSerializer
        )
    }

    @Provides
    @Singleton
    @UserProfileDataStore
    fun provideUserProfileDataStore(
        @ApplicationContext context: Context
    ): ProtoDataStoreManager<UserProfiles> {
        return ProtoDataStoreManager.create(
            context = context,
            fileName = "user_profiles.pb",
            serializer = UserProfileSerializer
        )
    }

    @Provides
    @Singleton
    fun provideDatastoreManager(
        @AppPreferencesDataStore appPreferencesStore: ProtoDataStoreManager<AppPreferences>
    ): DatastoreManager {
        return DatastoreManager(appPreferencesStore)
    }
}
