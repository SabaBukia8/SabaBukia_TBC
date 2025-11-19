package com.example.sababukia_tbc.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class RegisterResponseDTO(
    val id: Int,
    val token: String
)
