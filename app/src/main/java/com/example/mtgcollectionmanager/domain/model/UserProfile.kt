package com.example.mtgcollectionmanager.domain.model

data class UserProfile(
    val uid: String,
    val email: String,
    val nickname: String,
    val createdAt: Long,
    val collectionCount: Int = 0,
    val totalCards: Int = 0
)
