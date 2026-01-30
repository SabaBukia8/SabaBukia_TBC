package com.example.sababukia_tbc.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class StoryDto(
    val id: Int,
    val cover: String,
    val title: String
)
