package com.example.mtgcollectionmanager.domain.model

data class Card(
    val id: String,
    val name: String,
    val manaCost: String,
    val imageUrl: String,
    val type: String,
    val rarity: String,
    val setCode: String,
    val setName: String,
    val collectorNumber: String,
    val releasedAt: String,
    val colors: List<String>,
    val price: Double
)
