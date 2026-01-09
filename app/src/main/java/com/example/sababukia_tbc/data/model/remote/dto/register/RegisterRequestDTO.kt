package com.example.sababukia_tbc.data.model.remote.dto.register

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequestDTO(
    @SerialName("email")
    val email: String,
    @SerialName("password")
    val password: String
)
