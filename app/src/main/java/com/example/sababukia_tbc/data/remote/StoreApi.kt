package com.example.sababukia_tbc.data.remote

import com.example.sababukia_tbc.data.remote.dto.CategoryDto
import com.example.sababukia_tbc.data.remote.dto.EventDto
import retrofit2.http.GET

interface StoreApi {
    @GET("events")
    suspend fun getEvents(): List<EventDto>

    @GET("category")
    suspend fun getCategories(): List<CategoryDto>
}
