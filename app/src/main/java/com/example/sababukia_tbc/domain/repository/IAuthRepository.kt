package com.example.sababukia_tbc.domain.repository

import com.example.sababukia_tbc.domain.model.AuthResult
import com.example.sababukia_tbc.domain.model.LoginCredentials
import com.example.sababukia_tbc.domain.model.RegisterCredentials


interface IAuthRepository {
    suspend fun login(credentials: LoginCredentials): Result<AuthResult>
    suspend fun register(credentials: RegisterCredentials): Result<AuthResult>
    suspend fun saveAuthToken(token: String)
    suspend fun getAuthToken(): String?
    suspend fun clearAuthToken()
    suspend fun saveUsername(username: String)
    suspend fun getUsername(): String?
}
