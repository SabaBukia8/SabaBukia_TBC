package com.example.sababukia_tbc.data.remote.api

import com.example.sababukia_tbc.data.model.remote.PostDto
import com.example.sababukia_tbc.data.model.remote.StoryDto
import retrofit2.Response
import retrofit2.http.GET

interface FeedApiService {
    @GET("v1/0f76d541-3832-4a3c-927a-0593e060d6da")
    suspend fun getStories(): Response<List<StoryDto>>

    @GET("v1/1e3f40b1-19a5-4986-ad60-fdc80c27234b")
    suspend fun getPosts(): Response<List<PostDto>>
}
