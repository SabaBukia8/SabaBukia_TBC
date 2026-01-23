package com.example.sababukia_tbc.domain.model

sealed class ChatError : DomainError {
    data object Network : ChatError()
    data object Unknown : ChatError()
}
