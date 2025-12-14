package com.example.mtgcollectionmanager.domain.usecase.auth

import com.example.mtgcollectionmanager.domain.common.Resource
import com.example.mtgcollectionmanager.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ChangePasswordUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(
        currentPassword: String,
        newPassword: String
    ): Flow<Resource<Unit>> =
        repository.changePassword(currentPassword, newPassword)
}
