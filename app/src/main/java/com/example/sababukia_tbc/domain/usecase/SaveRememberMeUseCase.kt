package com.example.sababukia_tbc.domain.usecase

import com.example.sababukia_tbc.domain.repository.AuthRepository
import javax.inject.Inject

class SaveRememberMeUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(rememberMe: Boolean) {
        repository.saveRememberMe(rememberMe)
    }
}
