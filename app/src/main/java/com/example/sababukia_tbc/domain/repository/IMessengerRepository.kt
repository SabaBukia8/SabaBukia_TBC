package com.example.sababukia_tbc.domain.repository

import com.example.sababukia_tbc.domain.common.Resource
import com.example.sababukia_tbc.domain.model.ChatItem
import kotlinx.coroutines.flow.Flow

interface IMessengerRepository {
    fun getChats(): Flow<Resource<List<ChatItem>>>
}
