package com.example.sababukia_tbc.domain.model

sealed interface DomainError

sealed class Result<out T, out E : DomainError> {
    data class Success<T>(val data: T) : Result<T, Nothing>()
    data class Error<E : DomainError>(val error: E) : Result<Nothing, E>()
}

inline fun <T, E : DomainError> Result<T, E>.onSuccess(action: (T) -> Unit): Result<T, E> {
    if (this is Result.Success) action(data)
    return this
}

inline fun <T, E : DomainError> Result<T, E>.onError(action: (E) -> Unit): Result<T, E> {
    if (this is Result.Error) action(error)
    return this
}
