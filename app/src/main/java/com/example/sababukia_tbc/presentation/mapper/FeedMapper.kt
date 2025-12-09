package com.example.sababukia_tbc.presentation.mapper

import com.example.sababukia_tbc.domain.model.Post
import com.example.sababukia_tbc.domain.model.Story
import com.example.sababukia_tbc.presentation.model.PostUiModel
import com.example.sababukia_tbc.presentation.model.StoryUiModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun Story.toUi(): StoryUiModel = StoryUiModel(
    id = id,
    title = title,
    cover = cover
)

fun Post.toUi(): PostUiModel = PostUiModel(
    id = id,
    avatar = avatar,
    postDateFormatted = formatDate(postDate),
    fullName = "$firstName $lastName",
    images = images,
    commentsCountText = "$commentsCount Comments",
    likesCountText = "$likesCount Likes",
    postDesc = postDesc,
    canComment = canComment,
    canPostPhoto = canPostPhoto
)

private fun formatDate(epochMillis: Long): String {
    val date = Date(epochMillis)
    val formatter = SimpleDateFormat("d MMMM 'at' h:mm a", Locale.ENGLISH)
    return formatter.format(date)
}
