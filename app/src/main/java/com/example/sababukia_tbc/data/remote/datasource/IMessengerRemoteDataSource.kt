package com.example.sababukia_tbc.data.remote.datasource

import com.example.sababukia_tbc.data.remote.dto.ChatItemDTO

interface IMessengerRemoteDataSource {
    suspend fun getChats(): Result<List<ChatItemDTO>>
}
