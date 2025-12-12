package com.example.mtgcollectionmanager.presentation.mapper

import com.example.mtgcollectionmanager.domain.model.Category
import com.example.mtgcollectionmanager.presentation.model.CategoryUi

fun Category.toUi(): CategoryUi = CategoryUi(
    id = id,
    collectionId = collectionId,
    name = name,
    color = color,
    cardCount = cardCount
)
