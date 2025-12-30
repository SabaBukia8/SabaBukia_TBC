package com.example.sababukia_tbc.data.service

import com.example.sababukia_tbc.data.model.CategoryDto
import retrofit2.http.GET

interface CategoryService {
    @GET("0c08be03-49c2-493b-951c-6ba8a397dc72")
    suspend fun getCategories(): List<CategoryDto>
}
