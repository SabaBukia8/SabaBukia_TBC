package com.example.sababukia_tbc.data.repository

import com.example.sababukia_tbc.data.common.safeCall
import com.example.sababukia_tbc.data.remote.api.FeedApiService
import com.example.sababukia_tbc.data.remote.mapper.toDomain
import com.example.sababukia_tbc.domain.model.FeedError
import com.example.sababukia_tbc.domain.model.Post
import com.example.sababukia_tbc.domain.model.Result
import com.example.sababukia_tbc.domain.model.Story
import com.example.sababukia_tbc.domain.repository.FeedRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FeedRepositoryImpl @Inject constructor(
    private val apiService: FeedApiService
) : FeedRepository {

    override suspend fun getStories(): Result<List<Story>, FeedError> {
        return safeCall {
            apiService.getStories().toDomain()
        }
    }

    override suspend fun getPosts(): Result<List<Post>, FeedError> {
        return safeCall {
            apiService.getPosts().toDomain()
        }
    }
}
