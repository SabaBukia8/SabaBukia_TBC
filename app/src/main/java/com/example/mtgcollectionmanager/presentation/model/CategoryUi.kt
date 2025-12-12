package com.example.mtgcollectionmanager.presentation.model

data class CategoryUi(
    val id: Long,
    val collectionId: Long,
    val name: String,
    val color: String,
    val cardCount: Int
)
