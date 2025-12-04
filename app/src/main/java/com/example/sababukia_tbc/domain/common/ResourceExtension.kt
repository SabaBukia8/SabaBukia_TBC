package com.example.sababukia_tbc.domain.common

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

fun <T, R> Flow<Resource<T>>.asResource(transform: (T) -> R): Flow<Resource<R>> = map { resource ->
    when (resource) {
        is Resource.Success -> Resource.Success(transform(resource.data))
        is Resource.Error -> Resource.Error(resource.errorMessage)
        is Resource.Loading -> Resource.Loading(resource.isLoading)
    }
}
