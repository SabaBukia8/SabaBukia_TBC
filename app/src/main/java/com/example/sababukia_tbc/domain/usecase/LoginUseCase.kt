package com.example.sababukia_tbc.domain.usecase

import com.example.sababukia_tbc.domain.model.AuthResult
import com.example.sababukia_tbc.domain.model.LoginCredentials
import com.example.sababukia_tbc.domain.repository.IAuthRepository
import javax.inject.Inject


class LoginUseCase @Inject constructor(
    private val authRepository: IAuthRepository
) {

    suspend operator fun invoke(username: String, password: String): Result<AuthResult> {
        val credentials = LoginCredentials(username, password)
        return authRepository.login(credentials)
    }
}
