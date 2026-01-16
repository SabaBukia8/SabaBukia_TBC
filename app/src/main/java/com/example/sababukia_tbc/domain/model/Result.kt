package com.example.sababukia_tbc.domain.model


sealed class Result<out T, out E : DomainError> {
    data class Success<T>(val data: T) : Result<T, Nothing>()
    data class Error<E : DomainError>(val error: E) : Result<Nothing, E>()

    val isSuccess: Boolean get() = this is Success
    val isError: Boolean get() = this is Error

    fun getOrNull(): T? = (this as? Success)?.data
    fun errorOrNull(): E? = (this as? Error)?.error

    inline fun <R> map(transform: (T) -> R): Result<R, E> = when (this) {
        is Success -> Success(transform(data))
        is Error -> Error(error)
    }

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
