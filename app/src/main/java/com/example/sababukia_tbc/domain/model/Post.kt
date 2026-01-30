package com.example.sababukia_tbc.domain.model

data class Post(
    val id: Int,
    val owner: Owner,
    val images: List<String>,
    val postDate: Long,
    val title: String,
    val comments: Int,
    val likes: Int,
    val canComment: Boolean,
    val canPostPhoto: Boolean
) {
    data class Owner(
        val firstName: String,
        val lastName: String,
        val profile: String
    ) {
        val fullName: String get() = "$firstName $lastName"
    }
}
