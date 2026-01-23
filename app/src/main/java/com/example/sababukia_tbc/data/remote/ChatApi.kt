package com.example.sababukia_tbc.data.remote

import com.example.sababukia_tbc.data.remote.dto.ChatDto
import retrofit2.http.GET

interface ChatApi {
    @GET("chats")
    suspend fun getChats(): List<ChatDto>
}
