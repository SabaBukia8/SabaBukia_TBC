package com.example.sababukia_tbc.domain.usecase

import com.example.sababukia_tbc.domain.model.AuthResponse
import com.example.sababukia_tbc.domain.model.LoginRequest
import com.example.sababukia_tbc.domain.repository.IAuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val repository: IAuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<AuthResponse> {
        val request = LoginRequest(email = email, password = password)
        return repository.login(request)
    }
}
