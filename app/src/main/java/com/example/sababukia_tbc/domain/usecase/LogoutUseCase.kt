package com.example.sababukia_tbc.domain.usecase

import com.example.sababukia_tbc.domain.repository.IAuthRepository
import javax.inject.Inject


class LogoutUseCase @Inject constructor(
    private val authRepository: IAuthRepository
) {

    suspend operator fun invoke() {
        authRepository.clearAuthToken()
    }
}
