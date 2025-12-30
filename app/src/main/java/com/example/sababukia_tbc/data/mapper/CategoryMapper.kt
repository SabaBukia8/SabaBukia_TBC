package com.example.sababukia_tbc.data.mapper

import com.example.sababukia_tbc.data.model.CategoryDto
import com.example.sababukia_tbc.domain.model.Category

private const val INITIAL_DEPTH = 0

fun CategoryDto.toDomain(): Category = Category(
    id = id,
    name = name,
    nameDe = nameDe,
    createdAt = createdAt,
    orderId = orderId,
    depth = INITIAL_DEPTH,
    children = children.map { it.toDomain() }
)
