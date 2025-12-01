package com.example.sababukia_tbc.data.model.remote.dto.login

import kotlinx.serialization.Serializable

@Serializable
data class LoginResponseDTO(
    val token: String
)
