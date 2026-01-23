package com.example.sababukia_tbc.domain.repository

import com.example.sababukia_tbc.domain.model.Chat
import com.example.sababukia_tbc.domain.model.ChatError
import com.example.sababukia_tbc.domain.model.Result

interface ChatRepository {
    suspend fun getChats(): Result<List<Chat>, ChatError>
}
