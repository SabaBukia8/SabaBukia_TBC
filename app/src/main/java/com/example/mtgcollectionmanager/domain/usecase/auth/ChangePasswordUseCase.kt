package com.example.mtgcollectionmanager.domain.usecase.auth

import com.example.mtgcollectionmanager.domain.common.Resource
import com.example.mtgcollectionmanager.domain.repository.AccountRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ChangePasswordUseCase @Inject constructor(
    private val repository: AccountRepository
) {
    suspend operator fun invoke(
        currentPassword: String,
        newPassword: String
    ): Flow<Resource<Unit>> =
        repository.changePassword(currentPassword, newPassword)
}
