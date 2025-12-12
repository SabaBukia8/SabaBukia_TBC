package com.example.mtgcollectionmanager.data.model.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CardSearchResponseDto(
    @SerialName("data") val cards: List<CardDto>,
    @SerialName("total_cards") val totalCards: Int
)
