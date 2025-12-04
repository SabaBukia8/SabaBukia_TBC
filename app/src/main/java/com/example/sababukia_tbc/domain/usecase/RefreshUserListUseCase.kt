package com.example.sababukia_tbc.domain.usecase

import com.example.sababukia_tbc.domain.repository.UserListRepository
import javax.inject.Inject

class RefreshUserListUseCase @Inject constructor(
    private val repository: UserListRepository
) {
    suspend operator fun invoke() = repository.refreshUsers()
}
