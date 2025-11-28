package com.example.sababukia_tbc.data.repository

import com.example.sababukia_tbc.data.DatastoreManager
import com.example.sababukia_tbc.data.common.HandleResponse
import com.example.sababukia_tbc.data.remote.dto.LoginRequestDTO
import com.example.sababukia_tbc.data.remote.dto.RegisterRequestDTO
import com.example.sababukia_tbc.data.remote.dto.toDomain
import com.example.sababukia_tbc.data.remote.network.LoginApiService
import com.example.sababukia_tbc.data.remote.network.RegisterApiService
import com.example.sababukia_tbc.data.remote.network.UsersApiService
import com.example.sababukia_tbc.domain.common.Resource
import com.example.sababukia_tbc.domain.model.AuthResponse
import com.example.sababukia_tbc.domain.model.LoginRequest
import com.example.sababukia_tbc.domain.model.RegisterRequest
import com.example.sababukia_tbc.domain.model.User
import com.example.sababukia_tbc.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val loginApiService: LoginApiService,
    private val registerApiService: RegisterApiService,
    private val usersApiService: UsersApiService,
    private val datastoreManager: DatastoreManager
) : AuthRepository {

    override fun login(request: LoginRequest): Flow<Resource<AuthResponse>> {
        val loginRequestDTO = LoginRequestDTO(
            email = request.email,
            password = request.password
        )

        return HandleResponse.safeApiCall {
            loginApiService.login(loginRequestDTO)
        }.map { resource ->
            when (resource) {
                is Resource.Success -> Resource.Success(resource.data.toDomain())
                is Resource.Error -> Resource.Error(resource.errorMessage)
                is Resource.Loading -> Resource.Loading(resource.isLoading)
            }
        }
    }

    override fun register(request: RegisterRequest): Flow<Resource<AuthResponse>> {
        val registerRequestDTO = RegisterRequestDTO(
            email = request.email,
            password = request.password
        )

        return HandleResponse.safeApiCall {
            registerApiService.register(registerRequestDTO)
        }.map { resource ->
            when (resource) {
                is Resource.Success -> Resource.Success(resource.data.toDomain())
                is Resource.Error -> Resource.Error(resource.errorMessage)
                is Resource.Loading -> Resource.Loading(resource.isLoading)
            }
        }
    }

    override fun getUsers(page: Int): Flow<Resource<List<User>>> {
        return HandleResponse.safeApiCall {
            usersApiService.getUsers(page)
        }.map { resource ->
            when (resource) {
                is Resource.Success -> {
                    val users = resource.data.data.map { userDTO ->
                        User(
                            id = userDTO.id,
                            email = userDTO.email,
                            firstName = userDTO.firstName,
                            lastName = userDTO.lastName,
                            avatar = userDTO.avatar
                        )
                    }
                    Resource.Success(users)
                }
                is Resource.Error -> Resource.Error(resource.errorMessage)
                is Resource.Loading -> Resource.Loading(resource.isLoading)
            }
        }
    }

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
