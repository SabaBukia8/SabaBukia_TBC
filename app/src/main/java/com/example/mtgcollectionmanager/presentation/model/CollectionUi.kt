package com.example.mtgcollectionmanager.presentation.model

data class CollectionUi(
    val id: Long,
    val name: String,
    val description: String,
    val totalCards: Int,
    val totalValue: String, // Formatted price
    val createdDate: String // Formatted date
)
