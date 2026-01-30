package com.example.sababukia_tbc.data.remote.mapper

import com.example.sababukia_tbc.data.remote.dto.StoryDto
import com.example.sababukia_tbc.domain.model.Story

fun StoryDto.toDomain(): Story {
    return Story(
        id = id,
        cover = cover,
        title = title
    )
}

fun List<StoryDto>.toDomain(): List<Story> = map { it.toDomain() }
