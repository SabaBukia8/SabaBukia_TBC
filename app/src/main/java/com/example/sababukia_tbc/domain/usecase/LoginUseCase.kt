package com.example.sababukia_tbc.domain.usecase

import com.example.sababukia_tbc.domain.common.Resource
import com.example.sababukia_tbc.domain.model.AuthResponse
import com.example.sababukia_tbc.domain.repository.LoginRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val repository: LoginRepository
) {
    operator fun invoke(email: String, password: String): Flow<Resource<AuthResponse>> {
        return repository.login(email, password)
    }
}
