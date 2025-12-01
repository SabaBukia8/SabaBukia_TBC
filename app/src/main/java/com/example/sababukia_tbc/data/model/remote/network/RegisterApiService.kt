package com.example.sababukia_tbc.data.model.remote.network

import com.example.sababukia_tbc.data.model.remote.dto.register.RegisterRequestDTO
import com.example.sababukia_tbc.data.model.remote.dto.register.RegisterResponseDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface RegisterApiService {
    @POST("api/register")
    suspend fun register(@Body request: RegisterRequestDTO): Response<RegisterResponseDTO>
}
