package com.example.sababukia_tbc.data.repository

import com.example.sababukia_tbc.data.local.ILocalDataSource
import com.example.sababukia_tbc.data.remote.IAuthRemoteDataSource
import com.example.sababukia_tbc.data.remote.dto.LoginRequestDTO
import com.example.sababukia_tbc.data.remote.dto.RegisterRequestDTO
import com.example.sababukia_tbc.domain.model.AuthResponse
import com.example.sababukia_tbc.domain.model.LoginRequest
import com.example.sababukia_tbc.domain.model.RegisterRequest
import com.example.sababukia_tbc.domain.model.User
import com.example.sababukia_tbc.domain.repository.IAuthRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val remoteDataSource: IAuthRemoteDataSource,
    private val localDataSource: ILocalDataSource
) : IAuthRepository {

    override suspend fun login(request: LoginRequest): Result<AuthResponse> {
        val loginRequestDTO = LoginRequestDTO(
            email = request.email,
            password = request.password
        )

        return remoteDataSource.login(loginRequestDTO).map { responseDTO ->
            AuthResponse(token = responseDTO.token)
        }
    }

    override suspend fun register(request: RegisterRequest): Result<AuthResponse> {
        val registerRequestDTO = RegisterRequestDTO(
            email = request.email,
            password = request.password
        )

        return remoteDataSource.register(registerRequestDTO).map { responseDTO ->
            AuthResponse(
                token = responseDTO.token,
                id = responseDTO.id
            )
        }
    }

    override suspend fun getUsers(page: Int): Result<List<User>> {
        return remoteDataSource.getUsers(page).map { usersResponseDTO ->
            usersResponseDTO.data.map { userDTO ->
                User(
                    id = userDTO.id,
                    email = userDTO.email,
                    firstName = userDTO.firstName,
                    lastName = userDTO.lastName,
                    avatar = userDTO.avatar
                )
            }
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

    override suspend fun saveEmail(email: String) {
        localDataSource.saveEmail(email)
    }

    override suspend fun getEmail(): String? {
        return localDataSource.getEmail()
    }

    override suspend fun saveRememberMe(rememberMe: Boolean) {
        localDataSource.saveRememberMe(rememberMe)
    }

    override suspend fun getRememberMe(): Boolean {
        return localDataSource.getRememberMe()
    }

    override suspend fun clearAll() {
        localDataSource.clearAll()
    }
}
