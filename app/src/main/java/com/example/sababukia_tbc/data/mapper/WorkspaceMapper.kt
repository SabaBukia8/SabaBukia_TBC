package com.example.sababukia_tbc.data.mapper

import com.example.sababukia_tbc.data.model.remote.WorkspaceDto
import com.example.sababukia_tbc.domain.model.WorkspaceItem

fun WorkspaceDto.toDomain(): WorkspaceItem = WorkspaceItem(
    location = location,
    altitudeM = number.toIntOrNull() ?: 0,
    title = title,
    image = photo,
    stars = stars,
    price = price
)
