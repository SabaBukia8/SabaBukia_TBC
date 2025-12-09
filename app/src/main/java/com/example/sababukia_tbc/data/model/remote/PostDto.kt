package com.example.sababukia_tbc.data.model.remote

import kotlinx.serialization.Serializable

@Serializable
data class PostDto(
    val avatar: String,
    val postDate: Long,
    val firstName: String,
    val lastName: String,
    val images: List<String>,
    val commentsCount: Int,
    val likesCount: Int,
    val postDesc: String,
    val canComment: Boolean,
    val canPostPhoto: Boolean
)
