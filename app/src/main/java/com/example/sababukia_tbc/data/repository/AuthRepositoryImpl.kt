package com.example.sababukia_tbc.data.repository

import com.example.sababukia_tbc.data.DatastoreManager
import com.example.sababukia_tbc.data.common.HandleResponse
import com.example.sababukia_tbc.data.model.remote.dto.login.LoginRequestDTO
import com.example.sababukia_tbc.data.model.remote.dto.register.RegisterRequestDTO
import com.example.sababukia_tbc.data.mapper.toDomain
import com.example.sababukia_tbc.data.model.remote.network.LoginApiService
import com.example.sababukia_tbc.data.model.remote.network.RegisterApiService
import com.example.sababukia_tbc.data.model.remote.network.UsersApiService
import com.example.sababukia_tbc.domain.common.Resource
import com.example.sababukia_tbc.domain.common.asResource
import com.example.sababukia_tbc.domain.model.AuthResponse
import com.example.sababukia_tbc.domain.model.User
import com.example.sababukia_tbc.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val loginApiService: LoginApiService,
    private val registerApiService: RegisterApiService,
    private val usersApiService: UsersApiService,
    private val datastoreManager: DatastoreManager,
    private val handleResponse: HandleResponse
) : AuthRepository {

    override fun login(email: String, password: String): Flow<Resource<AuthResponse>> {
        val loginRequestDTO = LoginRequestDTO(
            email = email,
            password = password
        )

        return handleResponse.safeApiCall {
            loginApiService.login(loginRequestDTO)
        }.asResource { it.toDomain() }
    }

    override fun register(email: String, password: String): Flow<Resource<AuthResponse>> {
        val registerRequestDTO = RegisterRequestDTO(
            email = email,
            password = password
        )

        return handleResponse.safeApiCall {
            registerApiService.register(registerRequestDTO)
        }.asResource { it.toDomain() }
    }

    override fun getUsers(page: Int): Flow<Resource<List<User>>> {
        return handleResponse.safeApiCall {
            usersApiService.getUsers(page)
        }.asResource { response -> response.data.toDomain() }
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
        return datastoreManager.rememberMe.first()
    }

    override suspend fun clearAll() {
        datastoreManager.clearAll()
    }
}
