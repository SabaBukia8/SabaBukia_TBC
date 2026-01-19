package com.example.sababukia_tbc.data.mapper

import com.example.sababukia_tbc.data.remote.dto.CategoryDto
import com.example.sababukia_tbc.data.remote.dto.EventDto
import com.example.sababukia_tbc.domain.model.Category
import com.example.sababukia_tbc.domain.model.Event

fun EventDto.toDomain(): Event = Event(
    id = id,
    title = title,
    price = price,
    imageUrl = image,
    category = category
)

fun CategoryDto.toDomain(): Category = Category(
    id = id,
    name = category
)

fun List<EventDto>.toDomainEvents(): List<Event> = map { it.toDomain() }

fun List<CategoryDto>.toDomainCategories(): List<Category> = map { it.toDomain() }
