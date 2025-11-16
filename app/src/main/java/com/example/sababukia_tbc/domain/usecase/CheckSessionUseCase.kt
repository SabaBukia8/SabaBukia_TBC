package com.example.sababukia_tbc.domain.usecase

import com.example.sababukia_tbc.domain.repository.IAuthRepository
import javax.inject.Inject

class CheckSessionUseCase @Inject constructor(
    private val repository: IAuthRepository
) {
    suspend operator fun invoke(): Boolean {
        val token = repository.getAuthToken()
        val rememberMe = repository.getRememberMe()
        return !token.isNullOrEmpty() && rememberMe
    }
}
