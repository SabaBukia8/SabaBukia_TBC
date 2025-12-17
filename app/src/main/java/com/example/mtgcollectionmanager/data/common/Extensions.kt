package com.example.mtgcollectionmanager.data.common

import com.example.mtgcollectionmanager.domain.common.AppError
import com.example.mtgcollectionmanager.domain.common.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import java.net.SocketTimeoutException
import java.net.UnknownHostException

fun Exception.toAppError(default: AppError = AppError.Unknown(message)): AppError {
    return when (this) {
        is UnknownHostException -> AppError.Network.NoConnection
        is SocketTimeoutException -> AppError.Network.Timeout
        else -> default
    }
}

fun String?.toFirestoreId(fallback: Long): String {
    return this?.takeIf { it.isNotEmpty() } ?: fallback.toString()
}

fun String.toPositiveLongId(): Long {
    return hashCode().toLong().let { if (it < 0) -it else it }
}

fun <T> resourceFlow(
    block: suspend FlowCollector<Resource<T>>.() -> Unit
): Flow<Resource<T>> = flow {
    emit(Resource.Loading(true))
    try {
        block()
    } finally {
        emit(Resource.Loading(false))
    }
}