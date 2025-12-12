package com.example.mtgcollectionmanager.domain.model

data class Category(
    val id: Long = 0,
    val collectionId: Long,
    val name: String,
    val color: String,
    val createdDate: Long,
    val cardCount: Int = 0
)
