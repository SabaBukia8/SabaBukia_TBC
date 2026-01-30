package com.example.sababukia_tbc.data.common

import com.example.sababukia_tbc.domain.model.FeedError
import com.example.sababukia_tbc.domain.model.Result
import java.io.IOException

suspend fun <T> safeCall(call: suspend () -> T): Result<T, FeedError> {
    return try {
        Result.Success(call())
    } catch (e: IOException) {
        Result.Error(FeedError.Network)
    } catch (e: Exception) {
        Result.Error(FeedError.Unknown(e.message ?: "Unknown error"))
    }
}
