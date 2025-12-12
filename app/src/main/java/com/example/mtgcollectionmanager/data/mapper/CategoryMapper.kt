package com.example.mtgcollectionmanager.data.mapper

import com.example.mtgcollectionmanager.data.model.local.CategoryEntity
import com.example.mtgcollectionmanager.domain.model.Category

fun CategoryEntity.toDomain(cardCount: Int = 0): Category = Category(
    id = id,
    collectionId = collectionId,
    name = name,
    color = color,
    createdDate = createdDate,
    cardCount = cardCount
)

fun Category.toEntity(): CategoryEntity = CategoryEntity(
    id = id,
    collectionId = collectionId,
    name = name,
    color = color,
    createdDate = createdDate
)
