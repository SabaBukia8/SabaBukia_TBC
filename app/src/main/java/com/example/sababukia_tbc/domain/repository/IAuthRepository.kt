package com.example.sababukia_tbc.domain.repository

import com.example.sababukia_tbc.domain.model.AuthResponse
import com.example.sababukia_tbc.domain.model.LoginRequest
import com.example.sababukia_tbc.domain.model.RegisterRequest
import com.example.sababukia_tbc.domain.model.User

interface IAuthRepository {
    suspend fun login(request: LoginRequest): Result<AuthResponse>
    suspend fun register(request: RegisterRequest): Result<AuthResponse>
    suspend fun getUsers(page: Int): Result<List<User>>

    suspend fun saveAuthToken(token: String)
    suspend fun getAuthToken(): String?
    suspend fun clearAuthToken()

    suspend fun saveEmail(email: String)
    suspend fun getEmail(): String?

    suspend fun saveRememberMe(rememberMe: Boolean)
    suspend fun getRememberMe(): Boolean

    suspend fun clearAll()
}
