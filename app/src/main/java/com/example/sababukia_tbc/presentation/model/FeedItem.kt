package com.example.sababukia_tbc.presentation.model

sealed class FeedItem {
    data class Stories(val stories: List<StoryUiModel>) : FeedItem()
    data class Post(val post: PostUiModel) : FeedItem()
}
