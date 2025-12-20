package com.example.mtgcollectionmanager.data.remote.util

import com.example.mtgcollectionmanager.domain.common.AppError
import com.example.mtgcollectionmanager.domain.common.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


suspend fun <T> executeIfNetworkAvailable(
    networkManager: NetworkConnectivityManager,
    operation: suspend () -> Flow<Resource<T>>
): Flow<Resource<T>> = flow {
    emit(Resource.Loading(true))

    if (networkManager.isNetworkAvailable()) {
        operation().collect { emit(it) }
    } else {
        emit(Resource.Error(AppError.Network.NoConnection))
        emit(Resource.Loading(false))
    }
}


 fun <T> executeWithFallback(
    networkManager: NetworkConnectivityManager,
    networkOperation: suspend () -> Flow<Resource<T>>,
    fallbackOperation: suspend () -> Flow<Resource<T>>
): Flow<Resource<T>> = flow {
    emit(Resource.Loading(true))

    if (networkManager.isNetworkAvailable()) {
        try {
            networkOperation().collect { emit(it) }
        } catch (e: Exception) {

            fallbackOperation().collect { emit(it) }
        }
    } else {

        fallbackOperation().collect { emit(it) }
    }
}