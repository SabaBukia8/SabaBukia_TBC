package com.example.sababukia_tbc.data.model.remote.network

import com.example.sababukia_tbc.data.model.remote.dto.login.LoginRequestDTO
import com.example.sababukia_tbc.data.model.remote.dto.login.LoginResponseDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginApiService {
    @POST("api/login")
    suspend fun login(@Body request: LoginRequestDTO): Response<LoginResponseDTO>
}
