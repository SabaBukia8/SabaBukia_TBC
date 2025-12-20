package com.example.mtgcollectionmanager.domain.usecase.auth

import com.example.mtgcollectionmanager.domain.common.Resource
import com.example.mtgcollectionmanager.domain.repository.UserProfileRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UpdateNicknameUseCase @Inject constructor(
    private val repository: UserProfileRepository
) {
    suspend operator fun invoke(nickname: String): Flow<Resource<Unit>> =
        repository.updateNickname(nickname)
}
