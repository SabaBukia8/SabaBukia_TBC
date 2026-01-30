package com.example.sababukia_tbc.presentation.screen.feed

import androidx.lifecycle.viewModelScope
import com.example.sababukia_tbc.domain.model.FeedError
import com.example.sababukia_tbc.domain.model.Result
import com.example.sababukia_tbc.domain.usecase.GetPostsUseCase
import com.example.sababukia_tbc.domain.usecase.GetStoriesUseCase
import com.example.sababukia_tbc.presentation.common.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val getStoriesUseCase: GetStoriesUseCase,
    private val getPostsUseCase: GetPostsUseCase
) : BaseViewModel<FeedState, FeedEvent, FeedSideEffect>(FeedState()) {

    init {
        loadFeed()
    }

    override fun onEvent(event: FeedEvent) {
        when (event) {
            FeedEvent.LoadFeed -> loadFeed()
            FeedEvent.Retry -> loadFeed()
        }
    }

    private fun loadFeed() {
        viewModelScope.launch {
            updateState { copy(isLoading = true, storiesError = null, postsError = null) }

            val storiesDeferred = async { getStoriesUseCase() }
            val postsDeferred = async { getPostsUseCase() }

            val storiesResult = storiesDeferred.await()
            val postsResult = postsDeferred.await()

            var storiesError: String? = null
            var postsError: String? = null

            when (storiesResult) {
                is Result.Success -> {
                    updateState { copy(stories = storiesResult.data) }
                }
                is Result.Error -> {
                    storiesError = mapErrorToMessage(storiesResult.error)
                    updateState { copy(storiesError = storiesError) }
                }
            }

            when (postsResult) {
                is Result.Success -> {
                    updateState { copy(posts = postsResult.data) }
                }
                is Result.Error -> {
                    postsError = mapErrorToMessage(postsResult.error)
                    updateState { copy(postsError = postsError) }
                }
            }

            updateState { copy(isLoading = false) }

            val errorMessage = storiesError ?: postsError
            if (errorMessage != null) {
                emitSideEffect(FeedSideEffect.ShowError(errorMessage))
            }
        }
    }

    private fun mapErrorToMessage(error: FeedError): String {
        return when (error) {
            is FeedError.Network -> "Network error. Please check your connection."
            is FeedError.Unknown -> error.message
        }
    }
}
