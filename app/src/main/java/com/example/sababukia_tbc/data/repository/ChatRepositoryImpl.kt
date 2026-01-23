package com.example.sababukia_tbc.data.repository

import com.example.sababukia_tbc.data.common.safeCall
import com.example.sababukia_tbc.data.mapper.toDomain
import com.example.sababukia_tbc.data.remote.ChatApi
import com.example.sababukia_tbc.domain.model.Chat
import com.example.sababukia_tbc.domain.model.ChatError
import com.example.sababukia_tbc.domain.model.Result
import com.example.sababukia_tbc.domain.repository.ChatRepository
import java.io.IOException
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
    private val api: ChatApi
) : ChatRepository {

    override suspend fun getChats(): Result<List<Chat>, ChatError> = safeCall(
        exceptionMapper = ::mapException
    ) {
        api.getChats().map { it.toDomain() }
    }

    private fun mapException(e: Exception): ChatError = when (e) {
        is IOException -> ChatError.Network
        else -> ChatError.Unknown
    }
}
