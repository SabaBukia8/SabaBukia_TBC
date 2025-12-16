package com.example.mtgcollectionmanager.data.repository

import com.example.mtgcollectionmanager.data.remote.util.NetworkConnectivityManager
import com.example.mtgcollectionmanager.data.remote.util.executeWithFallback
import com.example.mtgcollectionmanager.domain.common.Resource
import com.example.mtgcollectionmanager.domain.repository.NetworkAwareRepository
import kotlinx.coroutines.flow.Flow

abstract class NetworkAwareRepositoryImpl(
    protected val networkConnectivityManager: NetworkConnectivityManager
) : NetworkAwareRepository {

    override suspend fun <T> executeNetworkOperationWithFallback(
        networkOperation: suspend () -> Flow<Resource<T>>,
        fallbackOperation: suspend () -> Flow<Resource<T>>
    ): Flow<Resource<T>> {
        return executeWithFallback(
            networkManager = networkConnectivityManager,
            networkOperation = networkOperation,
            fallbackOperation = fallbackOperation
        )
    }

    override fun isNetworkAvailable(): Boolean {
        return networkConnectivityManager.isNetworkAvailable()
    }
}