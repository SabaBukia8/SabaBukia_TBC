package com.example.sababukia_tbc.domain.repository

import com.example.sababukia_tbc.domain.model.FeedError
import com.example.sababukia_tbc.domain.model.Post
import com.example.sababukia_tbc.domain.model.Result
import com.example.sababukia_tbc.domain.model.Story

interface FeedRepository {
    suspend fun getStories(): Result<List<Story>, FeedError>
    suspend fun getPosts(): Result<List<Post>, FeedError>
}
