package com.example.sababukia_tbc.domain.usecase

import com.example.sababukia_tbc.domain.model.UserInfo
import com.example.sababukia_tbc.domain.repository.IAuthRepository
import javax.inject.Inject


class GetUserInfoUseCase @Inject constructor(
    private val authRepository: IAuthRepository
) {

    suspend operator fun invoke(): UserInfo {
        val username = authRepository.getUsername()
        return UserInfo(
            username = username,
            email = null,
            userId = null
        )
    }
}
