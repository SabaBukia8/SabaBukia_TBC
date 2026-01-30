package com.example.sababukia_tbc.data.remote.api

import com.example.sababukia_tbc.data.remote.dto.PostDto
import com.example.sababukia_tbc.data.remote.dto.StoryDto
import retrofit2.http.GET

interface FeedApiService {
    @GET("story")
    suspend fun getStories(): List<StoryDto>

    @GET("post")
    suspend fun getPosts(): List<PostDto>
}
