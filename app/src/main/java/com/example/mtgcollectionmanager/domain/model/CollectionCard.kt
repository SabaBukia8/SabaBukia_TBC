package com.example.mtgcollectionmanager.domain.model

data class CollectionCard(
    val cardId: String,
    val card: Card,
    val quantity: Int,
    val condition: CardCondition,
    val addedDate: Long,
    val notes: String
)
