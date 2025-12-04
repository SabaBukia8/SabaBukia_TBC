package com.example.sababukia_tbc.presentation.model

data class UserListItemUi(
    val id: Int,
    val fullName: String,
    val email: String,
    val activationStatus: Int,
    val lastActiveDescription: String,
    val lastActiveEpoch: Long,
    val profileImageUrl: String?
)
