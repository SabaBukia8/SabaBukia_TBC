package com.example.sababukia_tbc.domain.common

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

fun <D, R> Flow<Resource<D>>.mapResource(transform: (D) -> R): Flow<Resource<R>> =
    map { it.map(transform) }

fun <T> Resource<T>.onSuccess(action: (T) -> Unit): Resource<T> {
    if (this is Resource.Success) action(data)
    return this
}

fun <T> Resource<T>.onError(action: (ErrorType) -> Unit): Resource<T> {
    if (this is Resource.Error) action(error)
    return this
}

fun <T> Resource<T>.onLoading(action: (Boolean) -> Unit): Resource<T> {
    if (this is Resource.Loading) action(isLoading)
    return this
}
