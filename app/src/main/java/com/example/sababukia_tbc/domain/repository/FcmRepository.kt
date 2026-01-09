package com.example.sababukia_tbc.domain.repository

import com.example.sababukia_tbc.domain.common.Resource
import kotlinx.coroutines.flow.Flow

interface FcmRepository {
    suspend fun saveFcmToken(token: String)
    suspend fun getFcmToken(): String?
    fun getFcmTokenFlow(): Flow<String>
    suspend fun clearFcmToken()
    fun syncFcmTokenWithBackend(token: String): Flow<Resource<Boolean>>
}
