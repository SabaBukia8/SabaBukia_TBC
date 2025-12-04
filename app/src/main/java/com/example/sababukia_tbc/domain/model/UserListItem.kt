package com.example.sababukia_tbc.domain.model

data class UserListItem(
    val id: Int,
    val fullName: String,
    val email: String,
    val activationStatus: Int,
    val lastActiveDescription: String,
    val lastActiveEpoch: Long,
    val profileImageUrl: String?
)
