package com.example.sababukia_tbc.data.mapper

import com.example.sababukia_tbc.data.model.local.StoryEntity
import com.example.sababukia_tbc.data.model.remote.StoryDto
import com.example.sababukia_tbc.domain.model.Story

fun StoryDto.toEntity(): StoryEntity = StoryEntity(
    title = title,
    cover = cover
)

fun StoryEntity.toDomain(): Story = Story(
    id = id,
    title = title,
    cover = cover
)
