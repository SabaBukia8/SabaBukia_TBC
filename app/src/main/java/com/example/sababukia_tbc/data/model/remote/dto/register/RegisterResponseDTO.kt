package com.example.sababukia_tbc.data.model.remote.dto.register

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RegisterResponseDTO(
    @SerialName("id")
    val id: Int,
    @SerialName("token")
    val token: String
)
