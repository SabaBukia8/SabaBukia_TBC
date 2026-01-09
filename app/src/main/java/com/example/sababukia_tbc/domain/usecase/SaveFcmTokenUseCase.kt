package com.example.sababukia_tbc.domain.usecase

import com.example.sababukia_tbc.domain.repository.FcmRepository
import javax.inject.Inject

class SaveFcmTokenUseCase @Inject constructor(
    private val repository: FcmRepository
) {
    suspend operator fun invoke(token: String) {
        repository.saveFcmToken(token)
    }
}
