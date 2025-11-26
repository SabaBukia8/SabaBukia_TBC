package com.example.sababukia_tbc.data.remote.dto

import com.example.sababukia_tbc.domain.model.AuthResponse

fun LoginResponseDTO.toDomain(): AuthResponse = AuthResponse(
    token = token
)

fun RegisterResponseDTO.toDomain(): AuthResponse = AuthResponse(
    token = token,
    id = id
)
