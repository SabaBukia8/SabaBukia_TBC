package com.example.sababukia_tbc.data.model.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WorkspaceDto(
    @SerialName("location")
    val location: String,
    @SerialName("altitude_m")
    val altitudeM: Int,
    @SerialName("title")
    val title: String,
    @SerialName("image")
    val image: String,
    @SerialName("stars")
    val stars: Int
)
