package com.example.sababukia_tbc.presentation.model

data class PostUiModel(
    val id: Long,
    val avatar: String,
    val postDateFormatted: String,
    val fullName: String,
    val images: List<String>,
    val commentsCountText: String,
    val likesCountText: String,
    val postDesc: String,
    val canComment: Boolean,
    val canPostPhoto: Boolean
)
