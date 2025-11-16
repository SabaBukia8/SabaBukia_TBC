package com.example.sababukia_tbc.presentation.mapper

import com.example.sababukia_tbc.data.remote.dto.LoginRequestDTO
import com.example.sababukia_tbc.data.remote.dto.LoginResponseDTO
import com.example.sababukia_tbc.data.remote.dto.RegisterRequestDTO
import com.example.sababukia_tbc.data.remote.dto.RegisterResponseDTO
import com.example.sababukia_tbc.domain.model.AuthResult
import com.example.sababukia_tbc.domain.model.LoginCredentials
import com.example.sababukia_tbc.domain.model.RegisterCredentials

object AuthMapper {

    fun toLoginRequestDTO(credentials: LoginCredentials): LoginRequestDTO {
        return LoginRequestDTO(
            email = credentials.username,  // Map username to email for API compatibility
            password = credentials.password
        )
    }

    fun toRegisterRequestDTO(credentials: RegisterCredentials): RegisterRequestDTO {
        return RegisterRequestDTO(
            username = credentials.username,
            email = credentials.email,
            password = credentials.password
        )
    }

    fun toAuthResult(response: LoginResponseDTO): AuthResult {
        return AuthResult(
            token = response.token,
            userId = null
        )
    }

    fun toAuthResult(response: RegisterResponseDTO): AuthResult {
        return AuthResult(
            token = response.token,
            userId = response.id
        )
    }
}
