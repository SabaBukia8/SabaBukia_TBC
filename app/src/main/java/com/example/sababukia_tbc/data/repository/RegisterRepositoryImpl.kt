package com.example.sababukia_tbc.data.repository

import com.example.sababukia_tbc.data.common.HandleResponse
import com.example.sababukia_tbc.data.mapper.toDomain
import com.example.sababukia_tbc.data.model.remote.dto.register.RegisterRequestDTO
import com.example.sababukia_tbc.data.model.remote.network.RegisterApiService
import com.example.sababukia_tbc.domain.common.Resource
import com.example.sababukia_tbc.domain.common.asResource
import com.example.sababukia_tbc.domain.model.AuthResponse
import com.example.sababukia_tbc.domain.repository.RegisterRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RegisterRepositoryImpl @Inject constructor(
    private val registerApiService: RegisterApiService,
    private val handleResponse: HandleResponse
) : RegisterRepository {

    override fun register(email: String, password: String): Flow<Resource<AuthResponse>> {
        val registerRequestDTO = RegisterRequestDTO(
            email = email,
            password = password
        )

        return handleResponse.safeApiCall {
            registerApiService.register(registerRequestDTO)
        }.asResource { it.toDomain() }
    }
}
