package com.example.sababukia_tbc.domain.model

data class Chat(
    val id: Int,
    val image: String?,
    val owner: String,
    val lastMessage: String,
    val lastActive: String,
    val unreadMessages: Int,
    val isTyping: Boolean,
    val lastMessageType: MessageType
)

enum class MessageType {
    TEXT,
    VOICE,
    FILE
}
