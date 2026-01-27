package com.example.sababukia_tbc.data.model.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WorkspaceDto(
    @SerialName("id")
    val id: Int,
    @SerialName("title")
    val title: String,
    @SerialName("location")
    val location: String,
    @SerialName("number")
    val number: String,
    @SerialName("photo")
    val photo: String,
    @SerialName("price")
    val price: Int,
    @SerialName("stars")
    val stars: Int
)
