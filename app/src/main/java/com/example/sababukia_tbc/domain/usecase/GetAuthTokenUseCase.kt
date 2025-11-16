package com.example.sababukia_tbc.domain.usecase

import com.example.sababukia_tbc.domain.repository.IAuthRepository
import javax.inject.Inject


class GetAuthTokenUseCase @Inject constructor(
    private val authRepository: IAuthRepository
) {

    suspend operator fun invoke(): String? {
        return authRepository.getAuthToken()
    }
}
