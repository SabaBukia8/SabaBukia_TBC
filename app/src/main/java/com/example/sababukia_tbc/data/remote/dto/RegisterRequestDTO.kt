package com.example.sababukia_tbc.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequestDTO(
    val email: String,
    val password: String
)
