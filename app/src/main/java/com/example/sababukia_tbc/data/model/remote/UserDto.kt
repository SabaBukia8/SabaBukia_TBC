package com.example.sababukia_tbc.data.model.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    @SerialName("id")
    val id: Int,
    @SerialName("full_name")
    val fullName: String,
    @SerialName("email")
    val email: String,
    @SerialName("activation_status")
    val activationStatus: Int,
    @SerialName("last_active_description")
    val lastActiveDescription: String,
    @SerialName("last_active_epoch")
    val lastActiveEpoch: Long,
    @SerialName("profile_image_url")
    val profileImageUrl: String?
)
