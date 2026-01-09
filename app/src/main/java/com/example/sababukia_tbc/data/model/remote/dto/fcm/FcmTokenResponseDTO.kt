package com.example.sababukia_tbc.data.model.remote.dto.fcm

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FcmTokenResponseDTO(
    @SerialName("success")
    val success: Boolean,
    @SerialName("message")
    val message: String? = null
)
