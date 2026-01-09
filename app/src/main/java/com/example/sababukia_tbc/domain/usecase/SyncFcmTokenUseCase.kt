package com.example.sababukia_tbc.domain.usecase

import com.example.sababukia_tbc.domain.common.Resource
import com.example.sababukia_tbc.domain.repository.FcmRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SyncFcmTokenUseCase @Inject constructor(
    private val repository: FcmRepository
) {
    operator fun invoke(token: String): Flow<Resource<Boolean>> {
        return repository.syncFcmTokenWithBackend(token)
    }
}
