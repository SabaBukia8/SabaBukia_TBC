package com.example.sababukia_tbc.domain.usecase

import com.example.sababukia_tbc.domain.repository.FcmRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFcmTokenUseCase @Inject constructor(
    private val repository: FcmRepository
) {
    suspend fun getToken(): String? = repository.getFcmToken()

    fun getTokenFlow(): Flow<String> = repository.getFcmTokenFlow()
}
