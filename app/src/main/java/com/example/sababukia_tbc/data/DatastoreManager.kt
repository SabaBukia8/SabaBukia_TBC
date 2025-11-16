package com.example.sababukia_tbc.data

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.sababukia_tbc.data.local.DatastoreKeys
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = DatastoreKeys.PREFS_NAME)

@Singleton
class DatastoreManager @Inject constructor(private val context: Context) {

    private val onboardedKey: Preferences.Key<Boolean> =
        booleanPreferencesKey(DatastoreKeys.KEY_ONBOARDED)
    private val registeredUsernameKey: Preferences.Key<String> =
        stringPreferencesKey(DatastoreKeys.KEY_REGISTERED_USERNAME)
    private val registeredEmailKey: Preferences.Key<String> =
        stringPreferencesKey(DatastoreKeys.KEY_REGISTERED_EMAIL)
    private val registeredPasswordKey: Preferences.Key<String> =
        stringPreferencesKey(DatastoreKeys.KEY_REGISTERED_PASSWORD)
    private val authTokenKey: Preferences.Key<String> =
        stringPreferencesKey(DatastoreKeys.KEY_AUTH_TOKEN)
    private val userIdKey: Preferences.Key<String> =
        stringPreferencesKey(DatastoreKeys.KEY_USER_ID)
    private val rememberMeKey: Preferences.Key<Boolean> =
        booleanPreferencesKey(DatastoreKeys.KEY_REMEMBER_ME)

    val registeredUsername: Flow<String?> = context.dataStore.data
        .map { prefs -> prefs[registeredUsernameKey] }

    val registeredEmail: Flow<String?> = context.dataStore.data
        .map { prefs -> prefs[registeredEmailKey] }

    val registeredPassword: Flow<String?> = context.dataStore.data
        .map { prefs -> prefs[registeredPasswordKey] }

    val authToken: Flow<String?> = context.dataStore.data
        .map { prefs -> prefs[authTokenKey] }

    val userId: Flow<String?> = context.dataStore.data
        .map { prefs -> prefs[userIdKey] }

    val rememberMe: Flow<Boolean?> = context.dataStore.data
        .map { prefs -> prefs[rememberMeKey] }

    suspend fun setOnboarded(value: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[onboardedKey] = value
        }
    }

    suspend fun saveRegisteredCredentials(username: String, email: String, password: String) {
        context.dataStore.edit { prefs ->
            prefs[registeredUsernameKey] = username
            prefs[registeredEmailKey] = email
            prefs[registeredPasswordKey] = password
        }
    }

    suspend fun saveAuthToken(token: String, userId: String? = null) {
        context.dataStore.edit { prefs ->
            prefs[authTokenKey] = token
            userId?.let { prefs[userIdKey] = it }
        }
    }

    suspend fun clearAuthToken() {
        context.dataStore.edit { prefs ->
            prefs.remove(authTokenKey)
            prefs.remove(userIdKey)
        }
    }

    suspend fun saveRememberMe(rememberMe: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[rememberMeKey] = rememberMe
        }
    }

    suspend fun clearAll() {
        context.dataStore.edit { prefs ->
            prefs.clear()
        }
    }
}
