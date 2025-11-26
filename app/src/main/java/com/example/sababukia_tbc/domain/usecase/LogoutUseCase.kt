package com.example.sababukia_tbc.domain.usecase

import com.example.sababukia_tbc.domain.repository.AuthRepository
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke() {
        repository.clearAll()
    }
}
