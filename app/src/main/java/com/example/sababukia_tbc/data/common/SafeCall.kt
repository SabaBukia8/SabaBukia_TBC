package com.example.sababukia_tbc.data.common

import com.example.sababukia_tbc.domain.model.DomainError
import com.example.sababukia_tbc.domain.model.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend inline fun <E : DomainError, T> safeCall(
    crossinline exceptionMapper: (Exception) -> E,
    crossinline block: suspend () -> T
): Result<T, E> = withContext(Dispatchers.IO) {
    try {
        Result.Success(block())
    } catch (e: Exception) {
        Result.Error(exceptionMapper(e))
    }
}

suspend inline fun <E : DomainError, T : Any> safeCallNullable(
    nullError: E,
    crossinline exceptionMapper: (Exception) -> E,
    crossinline block: suspend () -> T?
): Result<T, E> = withContext(Dispatchers.IO) {
    try {
        val result = block()
        if (result != null) {
            Result.Success(result)
        } else {
            Result.Error(nullError)
        }
    } catch (e: Exception) {
        Result.Error(exceptionMapper(e))
    }
}
