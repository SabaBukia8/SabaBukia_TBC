package com.example.sababukia_tbc.domain.common

sealed class Resource<out T> {
    data class Success<out T>(val data: T) : Resource<T>()
    data class Error(val error: ErrorType) : Resource<Nothing>()
    data class Loading(val isLoading: Boolean = true) : Resource<Nothing>()

    fun <R> map(transform: (T) -> R): Resource<R> {
        return when (this) {
            is Success -> Success(transform(data))
            is Error -> Error(error)
            is Loading -> Loading(isLoading)
        }
    }

    fun getOrNull(): T? = (this as? Success)?.data
}
