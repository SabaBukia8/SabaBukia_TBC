package com.example.sababukia_tbc.domain.common

sealed class Resource<out T> {
    data class Success<out T>(val data: T) : Resource<T>()
    data class Error(val error: DomainError) : Resource<Nothing>()
    data object Loading : Resource<Nothing>()
}
