package com.example.sababukia_tbc.domain.model

sealed class FeedError : DomainError {
    data object Network : FeedError()
    data class Unknown(val message: String) : FeedError()
}
