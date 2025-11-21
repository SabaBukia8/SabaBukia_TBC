package com.example.sababukia_tbc.domain.repository

import com.example.sababukia_tbc.domain.model.ChatItem

interface IMessengerRepository {
    suspend fun getChats(): Result<List<ChatItem>>
}
