package com.example.sababukia_tbc.data.repository

import com.example.sababukia_tbc.data.remote.datasource.IMessengerRemoteDataSource
import com.example.sababukia_tbc.domain.model.ChatItem
import com.example.sababukia_tbc.domain.model.MessageType
import com.example.sababukia_tbc.domain.repository.IMessengerRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MessengerRepositoryImpl @Inject constructor(
    private val remoteDataSource: IMessengerRemoteDataSource
) : IMessengerRepository {

    override suspend fun getChats(): Result<List<ChatItem>> {
        return remoteDataSource.getChats().map { dtoList ->
            dtoList.map { dto ->
                ChatItem(
                    id = dto.id,
                    image = dto.image,
                    owner = dto.owner,
                    lastMessage = dto.lastMessage,
                    lastActive = dto.lastActive,
                    unreadMessages = dto.unreadMessages,
                    isTyping = dto.isTyping,
                    messageType = MessageType.fromString(dto.lastMessageType)
                )
            }
        }
    }
}
