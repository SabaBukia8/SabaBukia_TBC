package com.example.sababukia_tbc.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChatItemDTO(
    @SerialName("id")
    val id: Int,
    @SerialName("image")
    val image: String? = null,
    @SerialName("owner")
    val owner: String,
    @SerialName("last_message")
    val lastMessage: String,
    @SerialName("last_active")
    val lastActive: String,
    @SerialName("unread_messages")
    val unreadMessages: Int,
    @SerialName("is_typing")
    val isTyping: Boolean,
    @SerialName("laste_message_type")
    val lastMessageType: String
)
