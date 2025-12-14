package com.example.mtgcollectionmanager.presentation.model

data class CollectionUi(
    val id: Long,
    val name: String,
    val description: String,
    val totalCards: Int,
    val totalValue: String,
    val createdDate: String,
    val userId: String,
    val createdDateMillis: Long
)
