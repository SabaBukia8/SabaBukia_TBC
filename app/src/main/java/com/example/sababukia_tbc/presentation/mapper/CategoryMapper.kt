package com.example.sababukia_tbc.presentation.mapper

import com.example.sababukia_tbc.domain.model.Category
import com.example.sababukia_tbc.presentation.model.CategoryModel

fun Category.toPresentation(): CategoryModel = CategoryModel(
    id = id,
    name = name,
    depth = depth
)
