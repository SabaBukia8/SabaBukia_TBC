package com.example.mtgcollectionmanager.domain.model

data class Collection(
    val id: Long = 0,
    val name: String,
    val description: String,
    val createdDate: Long,
    val userId: String,
    val totalCards: Int = 0,
    val totalValue: Double = 0.0
)
