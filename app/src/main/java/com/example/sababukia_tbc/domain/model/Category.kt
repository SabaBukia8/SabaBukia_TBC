package com.example.sababukia_tbc.domain.model

data class Category(
    val id: String,
    val name: String,
    val nameDe: String?,
    val createdAt: String,
    val orderId: Int?,
    val depth: Int,
    val children: List<Category>
)
