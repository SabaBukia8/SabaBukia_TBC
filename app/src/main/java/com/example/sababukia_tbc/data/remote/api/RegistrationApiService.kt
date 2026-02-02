package com.example.sababukia_tbc.data.remote.api

import com.example.sababukia_tbc.data.remote.dto.FormFieldDto
import retrofit2.http.GET

interface RegistrationApiService {
    @GET(".")
    suspend fun getFormConfiguration(): List<List<FormFieldDto>>
}
