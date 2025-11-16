package com.example.sababukia_tbc.domain.model

sealed class DomainException(message: String) : Exception(message) {
    data class NetworkException(val originalMessage: String) :
        DomainException("Network error: $originalMessage")

    data class ServerException(val code: Int, val originalMessage: String) :
        DomainException("Server error $code: $originalMessage")

    data class ClientException(val code: Int, val originalMessage: String) :
        DomainException("Client error $code: $originalMessage")

    data class AuthException(val originalMessage: String) :
        DomainException("Authentication failed: $originalMessage")

    data class DataException(val originalMessage: String) :
        DomainException("Data error: $originalMessage")

    data class UnknownException(val originalMessage: String) :
        DomainException("Unknown error: $originalMessage")
}
