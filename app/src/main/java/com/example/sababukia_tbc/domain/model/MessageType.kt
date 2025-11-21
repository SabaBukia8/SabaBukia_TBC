package com.example.sababukia_tbc.domain.model

enum class MessageType(val value: String) {
    TEXT("text"),
    FILE("file"),
    VOICE("voice");

    companion object {
        fun fromString(value: String): MessageType {
            return entries.find { it.value == value } ?: TEXT
        }
    }
}
