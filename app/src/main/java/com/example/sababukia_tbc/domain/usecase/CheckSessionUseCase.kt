package com.example.sababukia_tbc.domain.usecase

import com.example.sababukia_tbc.domain.repository.UserPreferencesRepository
import javax.inject.Inject

class CheckSessionUseCase @Inject constructor(
    private val repository: UserPreferencesRepository
) {
    suspend operator fun invoke(): Boolean {
        val token = repository.getAuthToken()
        val rememberMe = repository.getRememberMe()
        return !token.isNullOrEmpty() && rememberMe
    }
}
