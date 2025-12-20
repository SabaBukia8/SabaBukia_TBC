package com.example.mtgcollectionmanager.domain.common

sealed class Resource<out T> {
    data class Success<out T>(val data: T) : Resource<T>()
    data class Error<out T>(val error: AppError) : Resource<T>()
    data class Loading<out T>(val isLoading: Boolean) : Resource<T>()

    companion object {
        fun <T> error(message: String): Resource<T> = Error(AppError.Unknown(message))
    }
}
