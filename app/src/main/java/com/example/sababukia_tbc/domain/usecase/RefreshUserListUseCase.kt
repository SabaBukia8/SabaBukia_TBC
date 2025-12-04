package com.example.sababukia_tbc.domain.usecase

import com.example.sababukia_tbc.data.mapper.toDomain
import com.example.sababukia_tbc.domain.common.asResource
import com.example.sababukia_tbc.domain.repository.UserListRepository
import javax.inject.Inject

class RefreshUserListUseCase @Inject constructor(
    private val repository: UserListRepository
) {
    operator fun invoke() = repository.refreshUsers()
        .asResource { dtos ->
            dtos.map { it.toDomain() }
        }
}
