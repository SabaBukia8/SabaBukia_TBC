package com.example.mtgcollectionmanager.presentation.model

data class CollectionCardUiModel(
    val cardId: String,
    val name: String,
    val imageUrl: String,
    val setInfo: String,
    val setCode: String,
    val colors: List<String>,
    val quantity: Int,
    val quantityDisplay: String,
    val condition: String,
    val priceFormatted: String,
    val totalValueFormatted: String,
    val addedDateFormatted: String,
    val notes: String
)
