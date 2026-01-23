package com.example.sababukia_tbc.data.mapper

import com.example.sababukia_tbc.data.remote.dto.ChatDto
import com.example.sababukia_tbc.domain.model.Chat
import com.example.sababukia_tbc.domain.model.MessageType

fun ChatDto.toDomain(): Chat = Chat(
    id = id,
    image = image,
    owner = owner,
    lastMessage = lastMessage,
    lastActive = lastActive,
    unreadMessages = unreadMessages,
    isTyping = isTyping,
    lastMessageType = when (lastMessageType.uppercase()) {
        "VOICE" -> MessageType.VOICE
        "FILE" -> MessageType.FILE
        else -> MessageType.TEXT
    }
)
