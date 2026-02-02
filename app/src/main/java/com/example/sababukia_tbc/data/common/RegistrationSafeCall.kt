package com.example.sababukia_tbc.data.common

import com.example.sababukia_tbc.domain.model.RegistrationError
import com.example.sababukia_tbc.domain.model.Result
import java.io.IOException

suspend fun <T> registrationSafeCall(call: suspend () -> T): Result<T, RegistrationError> {
    return try {
        Result.Success(call())
    } catch (e: IOException) {
        Result.Error(RegistrationError.Network)
    } catch (e: Exception) {
        Result.Error(RegistrationError.Unknown(e.message ?: "Unknown error"))
    }
}
