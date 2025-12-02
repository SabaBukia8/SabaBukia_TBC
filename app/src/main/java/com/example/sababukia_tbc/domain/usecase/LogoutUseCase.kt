package com.example.sababukia_tbc.domain.usecase

import com.example.sababukia_tbc.domain.repository.UserPreferencesRepository
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val repository: UserPreferencesRepository
) {
    suspend operator fun invoke() {
        repository.clearAll()
    }
}
