package com.example.sababukia_tbc.data.model.remote.network

import com.example.sababukia_tbc.data.model.remote.dto.fcm.FcmTokenRequestDTO
import com.example.sababukia_tbc.data.model.remote.dto.fcm.FcmTokenResponseDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface FcmApiService {
    @POST("api/fcm/token")
    suspend fun syncFcmToken(@Body request: FcmTokenRequestDTO): Response<FcmTokenResponseDTO>
}
