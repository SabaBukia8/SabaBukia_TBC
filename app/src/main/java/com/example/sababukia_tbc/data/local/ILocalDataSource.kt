package com.example.sababukia_tbc.data.local

import kotlinx.coroutines.flow.Flow

interface ILocalDataSource {
    suspend fun saveAuthToken(token: String)
    suspend fun getAuthToken(): String?
    fun getAuthTokenFlow(): Flow<String?>
    suspend fun clearAuthToken()

    suspend fun saveUsername(username: String)
    suspend fun getUsername(): String?

    suspend fun saveEmail(email: String)
    suspend fun getEmail(): String?

    suspend fun saveRememberMe(rememberMe: Boolean)
    suspend fun getRememberMe(): Boolean

    suspend fun clearAll()
}
