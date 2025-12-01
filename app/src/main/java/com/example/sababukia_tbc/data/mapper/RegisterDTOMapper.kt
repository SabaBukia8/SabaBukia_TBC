package com.example.sababukia_tbc.data.mapper

import com.example.sababukia_tbc.data.model.remote.dto.register.RegisterResponseDTO
import com.example.sababukia_tbc.domain.model.AuthResponse

fun RegisterResponseDTO.toDomain(): AuthResponse = AuthResponse(
    token = token,
    id = id
)
