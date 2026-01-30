package com.example.sababukia_tbc.domain.model

interface DomainError

sealed class Result<out T, out E : DomainError> {
    data class Success<T>(val data: T) : Result<T, Nothing>()
    data class Error<E : DomainError>(val error: E) : Result<Nothing, E>()
}
