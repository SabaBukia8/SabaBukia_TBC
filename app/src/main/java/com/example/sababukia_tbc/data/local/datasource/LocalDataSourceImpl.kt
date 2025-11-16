package com.example.sababukia_tbc.data.local.datasource

import com.example.sababukia_tbc.data.DatastoreManager
import com.example.sababukia_tbc.data.local.ILocalDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalDataSourceImpl @Inject constructor(
    private val datastoreManager: DatastoreManager
) : ILocalDataSource {

    override suspend fun saveAuthToken(token: String) {
        datastoreManager.saveAuthToken(token)
    }

    override suspend fun getAuthToken(): String? {
        return datastoreManager.authToken.first()
    }

    override fun getAuthTokenFlow(): Flow<String?> {
        return datastoreManager.authToken
    }

    override suspend fun clearAuthToken() {
        datastoreManager.clearAuthToken()
    }

    override suspend fun saveUsername(username: String) {
        val email = getEmail() ?: ""
        val password = "" // We don't want to store password, but maintaining existing structure
        datastoreManager.saveRegisteredCredentials(username, email, password)
    }

    override suspend fun getUsername(): String? {
        return datastoreManager.registeredUsername.first()
    }

    override suspend fun saveEmail(email: String) {
        val username = getUsername() ?: ""
        val password = "" // We don't want to store password, but maintaining existing structure
        datastoreManager.saveRegisteredCredentials(username, email, password)
    }

    override suspend fun getEmail(): String? {
        return datastoreManager.registeredEmail.first()
    }

    override suspend fun clearAll() {
        datastoreManager.clearAll()
    }
}
