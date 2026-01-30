package com.example.sababukia_tbc.domain.usecase

import com.example.sababukia_tbc.domain.model.FeedError
import com.example.sababukia_tbc.domain.model.Post
import com.example.sababukia_tbc.domain.model.Result
import com.example.sababukia_tbc.domain.repository.FeedRepository
import javax.inject.Inject

class GetPostsUseCase @Inject constructor(
    private val repository: FeedRepository
) {
    suspend operator fun invoke(): Result<List<Post>, FeedError> {
        return repository.getPosts()
    }
}
