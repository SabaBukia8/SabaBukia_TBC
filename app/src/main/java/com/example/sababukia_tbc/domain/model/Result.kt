package com.example.sababukia_tbc.domain.model


sealed class Result<out T, out E : DomainError> {
    data class Success<T>(val data: T) : Result<T, Nothing>()
    data class Error<E : DomainError>(val error: E) : Result<Nothing, E>()

    inline fun onSuccess(action: (T) -> Unit): Result<T, E> {
        if (this is Success) action(data)
        return this
    }

    inline fun onError(action: (E) -> Unit): Result<T, E> {
        if (this is Error) action(error)
        return this
    }
}

interface DomainError
