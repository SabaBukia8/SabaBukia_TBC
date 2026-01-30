package com.example.sababukia_tbc.presentation.screen.feed

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.sababukia_tbc.domain.model.Story
import com.example.sababukia_tbc.presentation.common.LocalSnackbarController
import com.example.sababukia_tbc.presentation.common.extensions.CollectWithLifecycle
import com.example.sababukia_tbc.presentation.components.ErrorView
import com.example.sababukia_tbc.presentation.components.LoadingIndicator
import com.example.sababukia_tbc.presentation.components.PostItem
import com.example.sababukia_tbc.presentation.components.StoryItem
import com.example.sababukia_tbc.presentation.theme.AppTheme

@Composable
fun FeedScreen(
    viewModel: FeedViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarController = LocalSnackbarController.current

    viewModel.sideEffect.CollectWithLifecycle { sideEffect ->
        when (sideEffect) {
            is FeedSideEffect.ShowError -> {
                snackbarController.showSnackbar(sideEffect.message)
            }
        }
    }

    FeedScreenContent(
        state = state,
        onRetry = { viewModel.onEvent(FeedEvent.Retry) }
    )
}

@Composable
private fun FeedScreenContent(
    state: FeedState,
    onRetry: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        when {
            state.isLoading && state.stories.isEmpty() && state.posts.isEmpty() -> {
                LoadingIndicator()
            }
            state.hasError && state.stories.isEmpty() && state.posts.isEmpty() -> {
                ErrorView(
                    message = state.errorMessage ?: "An error occurred",
                    onRetry = onRetry
                )
            }
            else -> {
                FeedContent(
                    state = state,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
private fun FeedContent(
    state: FeedState,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(vertical = AppTheme.spacing.spacing19),
        verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.spacing16)
    ) {
        if (state.stories.isNotEmpty()) {
            item {
                StoriesRow(
                    stories = state.stories,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        items(
            items = state.posts,
            key = { it.id }
        ) { post ->
            PostItem(
                post = post,
                modifier = Modifier.padding(horizontal = AppTheme.spacing.spacing19)
            )
        }
    }
}

@Composable
private fun StoriesRow(
    stories: List<Story>,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = AppTheme.spacing.spacing19),
        horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.spacing19)
    ) {
        items(
            items = stories,
            key = { it.id }
        ) { story ->
            StoryItem(story = story)
        }
    }
}
