package com.example.mtgcollectionmanager.domain.usecase.auth

import com.example.mtgcollectionmanager.domain.common.Resource
import com.example.mtgcollectionmanager.domain.model.UserProfile
import com.example.mtgcollectionmanager.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserProfileUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(): Flow<Resource<UserProfile>> =
        repository.getUserProfile()
}
