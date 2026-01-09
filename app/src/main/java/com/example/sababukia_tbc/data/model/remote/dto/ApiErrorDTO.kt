package com.example.sababukia_tbc.data.model.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApiErrorDTO(
    @SerialName("error")
    val error: String
)
