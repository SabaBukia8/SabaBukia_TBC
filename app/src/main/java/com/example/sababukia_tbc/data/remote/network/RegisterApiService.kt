package com.example.sababukia_tbc.data.remote.network

import com.example.sababukia_tbc.data.remote.dto.RegisterRequestDTO
import com.example.sababukia_tbc.data.remote.dto.RegisterResponseDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface RegisterApiService {
    @POST("api/register")
    suspend fun register(@Body request: RegisterRequestDTO): Response<RegisterResponseDTO>
}
