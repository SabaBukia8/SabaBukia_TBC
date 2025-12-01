package com.example.sababukia_tbc.domain.repository

import com.example.sababukia_tbc.domain.common.Resource
import com.example.sababukia_tbc.domain.model.AuthResponse
import com.example.sababukia_tbc.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun login(email: String, password: String): Flow<Resource<AuthResponse>>
    fun register(email: String, password: String): Flow<Resource<AuthResponse>>
    fun getUsers(page: Int): Flow<Resource<List<User>>>

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
