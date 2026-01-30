package com.example.sababukia_tbc.data.remote.mapper

import com.example.sababukia_tbc.data.remote.dto.PostDto
import com.example.sababukia_tbc.domain.model.Post

fun PostDto.toDomain(): Post {
    return Post(
        id = id,
        owner = Post.Owner(
            firstName = firstName,
            lastName = lastName,
            profile = avatar ?: ""
        ),
        images = images,
        postDate = postDate / 1000, // Convert milliseconds to seconds
        title = postDesc ?: "",
        comments = commentsCount,
        likes = likesCount,
        canComment = canComment,
        canPostPhoto = canPostPhoto
    )
}

fun List<PostDto>.toDomain(): List<Post> = map { it.toDomain() }
