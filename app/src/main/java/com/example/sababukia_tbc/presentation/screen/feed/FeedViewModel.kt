package com.example.sababukia_tbc.presentation.screen.feed

import androidx.lifecycle.viewModelScope
import com.example.sababukia_tbc.domain.common.Resource
import com.example.sababukia_tbc.domain.usecase.GetPostsUseCase
import com.example.sababukia_tbc.domain.usecase.GetStoriesUseCase
import com.example.sababukia_tbc.domain.usecase.RefreshFeedUseCase
import com.example.sababukia_tbc.presentation.common.BaseViewModel
import com.example.sababukia_tbc.presentation.mapper.toUi
import com.example.sababukia_tbc.presentation.model.PostUiModel
import com.example.sababukia_tbc.presentation.model.StoryUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val getStoriesUseCase: GetStoriesUseCase,
    private val getPostsUseCase: GetPostsUseCase,
    private val refreshFeedUseCase: RefreshFeedUseCase
) : BaseViewModel<FeedState, FeedEvent, FeedSideEffect>(FeedState()) {

    init {
        loadFeed()
        observeFeedData()
    }

    override fun onEvent(event: FeedEvent) {
        when (event) {
            is FeedEvent.RefreshFeed -> loadFeed()
        }
    }

    private fun observeFeedData() {
        viewModelScope.launch {
            combine(
                getStoriesUseCase(),
                getPostsUseCase()
            ) { storiesResource, postsResource ->
                Pair(storiesResource, postsResource)
            }.collect { (storiesResource, postsResource) ->
                val stories = if (storiesResource is Resource.Success) {
                    storiesResource.data.map { it.toUi() }
                } else emptyList()

                val posts = if (postsResource is Resource.Success) {
                    postsResource.data.map { it.toUi() }
                } else emptyList()

                updateState { currentState ->
                    currentState.copy(
                        stories = stories,
                        posts = posts,
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun loadFeed() {
        viewModelScope.launch {
            updateState { it.copy(isLoading = true) }
            try {
                refreshFeedUseCase()
            } catch (e: Exception) {
                updateState { it.copy(isLoading = false) }
                emitSideEffect(FeedSideEffect.ShowError(e.message))
            }
        }
    }
}

data class FeedState(
    val stories: List<StoryUiModel> = emptyList(),
    val posts: List<PostUiModel> = emptyList(),
    val isLoading: Boolean = false
)

sealed interface FeedEvent {
    data object RefreshFeed : FeedEvent
}

sealed interface FeedSideEffect {
    data class ShowError(val message: String?) : FeedSideEffect
}
