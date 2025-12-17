package com.example.mtgcollectionmanager.domain.repository

import com.example.mtgcollectionmanager.domain.common.Resource
import kotlinx.coroutines.flow.Flow

interface NetworkAwareRepository {

    suspend fun <T> executeNetworkOperationWithFallback(
        networkOperation: suspend () -> Flow<Resource<T>>,
        fallbackOperation: suspend () -> Flow<Resource<T>>
    ): Flow<Resource<T>>

    fun isNetworkAvailable(): Boolean

    suspend fun syncFromRemote(): Result<Unit> = Result.success(Unit)
}