package com.example.sababukia_tbc.data.model.remote.dto.fcm

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FcmTokenRequestDTO(
    @SerialName("token")
    val token: String,
    @SerialName("device_id")
    val deviceId: String,
    @SerialName("platform")
    val platform: String = "android"
)
