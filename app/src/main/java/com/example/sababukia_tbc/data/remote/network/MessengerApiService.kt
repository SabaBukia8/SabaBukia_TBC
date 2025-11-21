package com.example.sababukia_tbc.data.remote.network

import com.example.sababukia_tbc.data.remote.dto.ChatItemDTO
import retrofit2.Response
import retrofit2.http.GET

interface MessengerApiService {
    @GET("d7d9436b-21c5-43f7-82f9-2334163351cf")
    suspend fun getChats(): Response<List<ChatItemDTO>>
}
