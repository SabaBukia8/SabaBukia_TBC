package com.example.sababukia_tbc.domain.usecase

import com.example.sababukia_tbc.domain.model.AuthResult
import com.example.sababukia_tbc.domain.model.RegisterCredentials
import com.example.sababukia_tbc.domain.repository.IAuthRepository
import javax.inject.Inject


class RegisterUseCase @Inject constructor(
    private val authRepository: IAuthRepository
) {
    suspend operator fun invoke(username: String, email: String, password: String): Result<AuthResult> {
        val credentials = RegisterCredentials(username, email, password)
        return authRepository.register(credentials)
    }
}
