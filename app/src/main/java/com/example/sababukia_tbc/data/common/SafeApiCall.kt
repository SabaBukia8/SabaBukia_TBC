package com.example.sababukia_tbc.data.common

import com.example.sababukia_tbc.domain.common.DomainError
import com.example.sababukia_tbc.domain.common.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException


suspend fun <T> safeDatabaseCall(dbCall: suspend () -> T): Resource<T> {
    return try {
        withContext(Dispatchers.IO) {
            Resource.Success(dbCall())
        }
    } catch (throwable: Throwable) {
        Resource.Error(DomainError.DatabaseError)
    }
}


suspend fun <T> safeLocationServiceCall(locationCall: suspend () -> T): Resource<T> {
    return try {
        withContext(Dispatchers.IO) {
            Resource.Success(locationCall())
        }
    } catch (throwable: Throwable) {
        when (throwable) {
            is SecurityException -> Resource.Error(DomainError.LocationPermissionDenied)
            is IOException -> Resource.Error(DomainError.NetworkError)
            else -> Resource.Error(DomainError.LocationServicesUnavailable)
        }
    }
}


suspend fun <T> safeCall(call: suspend () -> T): Resource<T> {
    return try {
        withContext(Dispatchers.IO) {
            Resource.Success(call())
        }
    } catch (throwable: Throwable) {
        Resource.Error(DomainError.GeneralError(throwable))
    }
}