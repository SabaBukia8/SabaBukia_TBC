package com.example.sababukia_tbc.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class PostDto(
    val id: Int,
    val avatar: String? = null,
    val postDate: Long,
    val firstName: String,
    val lastName: String,
    val images: List<String> = emptyList(),
    val commentsCount: Int = 0,
    val likesCount: Int = 0,
    val postDesc: String? = null,
    val canComment: Boolean = true,
    val canPostPhoto: Boolean = true
)
