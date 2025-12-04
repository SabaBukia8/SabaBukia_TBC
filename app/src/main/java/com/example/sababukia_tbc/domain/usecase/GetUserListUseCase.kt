package com.example.sababukia_tbc.domain.usecase

import com.example.sababukia_tbc.domain.model.UserListItem
import com.example.sababukia_tbc.domain.repository.UserListRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserListUseCase @Inject constructor(
    private val repository: UserListRepository
) {
    operator fun invoke(): Flow<List<UserListItem>> = repository.getUsers()
}
