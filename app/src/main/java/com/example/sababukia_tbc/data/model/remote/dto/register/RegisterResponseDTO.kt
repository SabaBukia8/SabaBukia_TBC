package com.example.sababukia_tbc.data.model.remote.dto.register

import kotlinx.serialization.Serializable

@Serializable
data class RegisterResponseDTO(
    val id: Int,
    val token: String
)
