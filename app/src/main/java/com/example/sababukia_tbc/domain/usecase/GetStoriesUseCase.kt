package com.example.sababukia_tbc.domain.usecase

import com.example.sababukia_tbc.domain.model.FeedError
import com.example.sababukia_tbc.domain.model.Result
import com.example.sababukia_tbc.domain.model.Story
import com.example.sababukia_tbc.domain.repository.FeedRepository
import javax.inject.Inject

class GetStoriesUseCase @Inject constructor(
    private val repository: FeedRepository
) {
    suspend operator fun invoke(): Result<List<Story>, FeedError> {
        return repository.getStories()
    }
}
