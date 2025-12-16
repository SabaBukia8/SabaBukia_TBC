package com.example.mtgcollectionmanager.presentation.model

data class CardUiModel(
    val cardId: String,
    val name: String,
    val manaCost: String,
    val imageUrl: String,
    val type: String,
    val rarity: String,
    val setCode: String,
    val setName: String,
    val collectorNumber: String,
    val releasedAt: String,
    val setInfo: String,
    val colorsDisplay: String,
    val priceFormatted: String,
    val marketPrices: List<MarketPriceUiModel> = emptyList()
)

data class MarketPriceUiModel(
    val marketName: String,
    val normalPrice: String,
    val foilPrice: String,
    val purchaseUrl: String?
)
