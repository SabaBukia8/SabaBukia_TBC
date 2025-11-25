package com.example.sababukia_tbc.data.remote.dto

import com.example.sababukia_tbc.domain.model.ChatItem
import com.example.sababukia_tbc.domain.model.MessageType

fun ChatItemDTO.toDomain(): ChatItem = ChatItem(
    id = id,
    image = image,
    owner = owner,
    lastMessage = lastMessage,
    lastActive = lastActive,
    unreadMessages = unreadMessages,
    isTyping = isTyping,
    messageType = MessageType.fromString(lastMessageType)
)

fun List<ChatItemDTO>.toDomain(): List<ChatItem> = map { it.toDomain() }
