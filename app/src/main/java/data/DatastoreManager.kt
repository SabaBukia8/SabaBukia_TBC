package data

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import util.DatastoreKeys

private val Context.dataStore by preferencesDataStore(name = DatastoreKeys.PREFS_NAME)

class DatastoreManager(private val context: Context) {

    private val onboardedKey: Preferences.Key<Boolean> =
        booleanPreferencesKey(DatastoreKeys.KEY_ONBOARDED)
    private val registeredUsernameKey: Preferences.Key<String> =
        stringPreferencesKey(DatastoreKeys.KEY_REGISTERED_USERNAME)
    private val registeredEmailKey: Preferences.Key<String> =
        stringPreferencesKey(DatastoreKeys.KEY_REGISTERED_EMAIL)
    private val registeredPasswordKey: Preferences.Key<String> =
        stringPreferencesKey(DatastoreKeys.KEY_REGISTERED_PASSWORD)

    val registeredUsername: Flow<String?> = context.dataStore.data
        .map { prefs -> prefs[registeredUsernameKey] }

    val registeredEmail: Flow<String?> = context.dataStore.data
        .map { prefs -> prefs[registeredEmailKey] }

    val registeredPassword: Flow<String?> = context.dataStore.data
        .map { prefs -> prefs[registeredPasswordKey] }

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
}
