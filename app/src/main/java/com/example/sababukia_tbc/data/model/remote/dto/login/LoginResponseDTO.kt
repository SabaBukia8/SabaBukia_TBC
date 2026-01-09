package com.example.sababukia_tbc.data.model.remote.dto.login

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginResponseDTO(
    @SerialName("token")
    val token: String
)
