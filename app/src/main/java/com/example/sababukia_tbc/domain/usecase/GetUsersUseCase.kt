package com.example.sababukia_tbc.domain.usecase

import com.example.sababukia_tbc.domain.common.Resource
import com.example.sababukia_tbc.domain.model.PaginatedData
import com.example.sababukia_tbc.domain.model.User
import com.example.sababukia_tbc.domain.repository.UsersRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUsersUseCase @Inject constructor(
    private val repository: UsersRepository
) {
    operator fun invoke(page: Int): Flow<Resource<PaginatedData<User>>> = repository.getUsers(page)
}
