package com.example.sababukia_tbc.data.model.remote.dto.login

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequestDTO(
    val email: String,
    val password: String
)
