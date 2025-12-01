package com.example.sababukia_tbc.data.model.remote.dto.register

import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequestDTO(
    val email: String,
    val password: String
)
