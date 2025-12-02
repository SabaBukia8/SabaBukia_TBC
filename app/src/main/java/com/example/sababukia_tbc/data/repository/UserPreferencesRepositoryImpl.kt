package com.example.sababukia_tbc.data.repository

import com.example.sababukia_tbc.data.DatastoreManager
import com.example.sababukia_tbc.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferencesRepositoryImpl @Inject constructor(
    private val datastoreManager: DatastoreManager
) : UserPreferencesRepository {

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
        datastoreManager.saveRegisteredCredentials(username, email)
    }

    override suspend fun getUsername(): String? {
        return datastoreManager.registeredUsername.first()
    }

    override suspend fun saveEmail(email: String) {
        val username = getUsername() ?: ""
        datastoreManager.saveRegisteredCredentials(username, email)
    }

    override suspend fun getEmail(): String? {
        return datastoreManager.registeredEmail.first()
    }

    override suspend fun saveRememberMe(rememberMe: Boolean) {
        datastoreManager.saveRememberMe(rememberMe)
    }

    override suspend fun getRememberMe(): Boolean {
        return datastoreManager.rememberMe.first() ?: false
    }

    override suspend fun clearAll() {
        datastoreManager.clearAll()
    }
}
