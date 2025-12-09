package com.example.sababukia_tbc.domain.usecase

import com.example.sababukia_tbc.domain.repository.PostRepository
import com.example.sababukia_tbc.domain.repository.StoryRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

class RefreshFeedUseCase @Inject constructor(
    private val storyRepository: StoryRepository,
    private val postRepository: PostRepository
) {
    suspend operator fun invoke() = coroutineScope {
        val storiesDeferred = async { storyRepository.refreshStories() }
        val postsDeferred = async { postRepository.refreshPosts() }

        storiesDeferred.await()
        postsDeferred.await()
    }
}
