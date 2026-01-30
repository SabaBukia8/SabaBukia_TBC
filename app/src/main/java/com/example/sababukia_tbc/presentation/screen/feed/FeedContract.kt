package com.example.sababukia_tbc.presentation.screen.feed

import com.example.sababukia_tbc.domain.model.Post
import com.example.sababukia_tbc.domain.model.Story

data class FeedState(
    val isLoading: Boolean = false,
    val stories: List<Story> = emptyList(),
    val posts: List<Post> = emptyList(),
    val storiesError: String? = null,
    val postsError: String? = null
) {
    val hasError: Boolean get() = storiesError != null || postsError != null
    val errorMessage: String? get() = storiesError ?: postsError
}

sealed interface FeedEvent {
    data object LoadFeed : FeedEvent
    data object Retry : FeedEvent
}

sealed interface FeedSideEffect {
    data class ShowError(val message: String) : FeedSideEffect
}
