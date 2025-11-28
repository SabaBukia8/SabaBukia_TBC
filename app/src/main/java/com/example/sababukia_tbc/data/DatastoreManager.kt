package com.example.sababukia_tbc.data

import com.example.sababukia_tbc.AppPreferences
import com.example.sababukia_tbc.data.local.datastore.ProtoDataStoreManager
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DatastoreManager @Inject constructor(
    private val appPreferencesStore: ProtoDataStoreManager<AppPreferences>
) {

    val registeredUsername: Flow<String> = appPreferencesStore.read { it.registeredUsername }

    val registeredEmail: Flow<String> = appPreferencesStore.read { it.registeredEmail }

    val authToken: Flow<String> = appPreferencesStore.read { it.authToken }

    val rememberMe: Flow<Boolean> = appPreferencesStore.read { it.rememberMe }

    suspend fun saveRegisteredCredentials(username: String, email: String) {
        appPreferencesStore.write { preferences ->
            preferences.toBuilder()
                .setRegisteredUsername(username)
                .setRegisteredEmail(email)
                .build()
        }
    }

    suspend fun saveAuthToken(token: String) {
        appPreferencesStore.write { preferences ->
            preferences.toBuilder()
                .setAuthToken(token)
                .build()
        }
    }

    suspend fun clearAuthToken() {
        appPreferencesStore.write { preferences ->
            preferences.toBuilder()
                .clearAuthToken()
                .build()
        }
    }

    suspend fun saveRememberMe(rememberMe: Boolean) {
        appPreferencesStore.write { preferences ->
            preferences.toBuilder()
                .setRememberMe(rememberMe)
                .build()
        }
    }

    suspend fun clearAll() {
        appPreferencesStore.clear(AppPreferences.getDefaultInstance())
    }
}
