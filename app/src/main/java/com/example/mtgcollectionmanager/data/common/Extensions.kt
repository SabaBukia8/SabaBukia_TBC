package com.example.mtgcollectionmanager.data.common

import com.example.mtgcollectionmanager.domain.common.AppError
import com.example.mtgcollectionmanager.domain.common.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * Converts an exception to an appropriate AppError
 */
fun Exception.toAppError(default: AppError = AppError.Unknown(message)): AppError {
    return when (this) {
        is UnknownHostException -> AppError.Network.NoConnection
        is SocketTimeoutException -> AppError.Network.Timeout
        else -> default
    }
}

/**
 * Converts a nullable string to a Firestore ID, using fallback if null or empty
 */
fun String?.toFirestoreId(fallback: Long): String {
    return this?.takeIf { it.isNotEmpty() } ?: fallback.toString()
}

/**
 * Converts a string to a positive long ID
 */
fun String.toPositiveLongId(): Long {
    return hashCode().toLong().let { if (it < 0) -it else it }
}

/**
 * Creates a Flow that automatically handles loading state and error handling
 */
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