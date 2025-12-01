package com.example.sababukia_tbc.data.mapper

import com.example.sababukia_tbc.data.model.remote.dto.login.LoginResponseDTO
import com.example.sababukia_tbc.domain.model.AuthResponse

fun LoginResponseDTO.toDomain(): AuthResponse = AuthResponse(
    token = token
)
