package com.example.sababukia_tbc.data.mapper

import com.example.sababukia_tbc.data.model.local.PostEntity
import com.example.sababukia_tbc.data.model.remote.PostDto
import com.example.sababukia_tbc.domain.model.Post
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val json = Json { ignoreUnknownKeys = true }

fun PostDto.toEntity(): PostEntity = PostEntity(
    avatar = avatar,
    postDate = postDate,
    firstName = firstName,
    lastName = lastName,
    images = json.encodeToString(images),
    commentsCount = commentsCount,
    likesCount = likesCount,
    postDesc = postDesc,
    canComment = canComment,
    canPostPhoto = canPostPhoto
)

fun PostEntity.toDomain(): Post = Post(
    id = id,
    avatar = avatar,
    postDate = postDate,
    firstName = firstName,
    lastName = lastName,
    images = json.decodeFromString(images),
    commentsCount = commentsCount,
    likesCount = likesCount,
    postDesc = postDesc,
    canComment = canComment,
    canPostPhoto = canPostPhoto
)
