package com.example.sababukia_tbc.data.common

import com.example.sababukia_tbc.domain.model.Result
import com.example.sababukia_tbc.domain.model.WorkspaceError
import java.io.IOException

suspend fun <T> safeCall(
    call: suspend () -> T
): Result<T, WorkspaceError> {
    return try {
        Result.Success(call())
    } catch (e: IOException) {
        Result.Error(WorkspaceError.Network)
    } catch (e: Exception) {
        Result.Error(WorkspaceError.Unknown(e.message ?: "Unknown error occurred"))
    }
}
