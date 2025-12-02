package com.example.sababukia_tbc.data.repository

import com.example.sababukia_tbc.data.common.HandleResponse
import com.example.sababukia_tbc.data.mapper.toDomain
import com.example.sababukia_tbc.data.model.remote.dto.login.LoginRequestDTO
import com.example.sababukia_tbc.data.model.remote.network.LoginApiService
import com.example.sababukia_tbc.domain.common.Resource
import com.example.sababukia_tbc.domain.common.asResource
import com.example.sababukia_tbc.domain.model.AuthResponse
import com.example.sababukia_tbc.domain.repository.LoginRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LoginRepositoryImpl @Inject constructor(
    private val loginApiService: LoginApiService,
    private val handleResponse: HandleResponse
) : LoginRepository {

    override fun login(email: String, password: String): Flow<Resource<AuthResponse>> {
        val loginRequestDTO = LoginRequestDTO(
            email = email,
            password = password
        )

        return handleResponse.safeApiCall {
            loginApiService.login(loginRequestDTO)
        }.asResource { it.toDomain() }
    }
}
