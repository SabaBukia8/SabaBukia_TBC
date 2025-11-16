package com.example.sababukia_tbc.data.repository

import com.example.sababukia_tbc.data.local.ILocalDataSource
import com.example.sababukia_tbc.data.remote.IRemoteDataSource
import com.example.sababukia_tbc.domain.model.AuthResult
import com.example.sababukia_tbc.domain.model.LoginCredentials
import com.example.sababukia_tbc.domain.model.RegisterCredentials
import com.example.sababukia_tbc.domain.repository.IAuthRepository
import com.example.sababukia_tbc.presentation.mapper.AuthMapper
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val remoteDataSource: IRemoteDataSource,
    private val localDataSource: ILocalDataSource
) : IAuthRepository {

    override suspend fun login(credentials: LoginCredentials): Result<AuthResult> {
        val requestDTO = AuthMapper.toLoginRequestDTO(credentials)
        return remoteDataSource.login(requestDTO).mapCatching { responseDTO ->
            val authResult = AuthMapper.toAuthResult(responseDTO)
            saveAuthToken(authResult.token)
            authResult
        }
    }

    override suspend fun register(credentials: RegisterCredentials): Result<AuthResult> {
        val requestDTO = AuthMapper.toRegisterRequestDTO(credentials)
        return remoteDataSource.register(requestDTO).mapCatching { responseDTO ->
            val authResult = AuthMapper.toAuthResult(responseDTO)
            saveAuthToken(authResult.token)
            saveUsername(credentials.username)
            authResult
        }
    }

    override suspend fun saveAuthToken(token: String) {
        localDataSource.saveAuthToken(token)
    }

    override suspend fun getAuthToken(): String? {
        return localDataSource.getAuthToken()
    }

    override suspend fun clearAuthToken() {
        localDataSource.clearAuthToken()
    }

    override suspend fun saveUsername(username: String) {
        localDataSource.saveUsername(username)
    }

    override suspend fun getUsername(): String? {
        return localDataSource.getUsername()
    }
}
