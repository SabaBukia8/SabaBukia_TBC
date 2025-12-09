package com.example.sababukia_tbc.data.model.remote

import kotlinx.serialization.Serializable

@Serializable
data class StoryDto(
    val title: String,
    val cover: String
)
