package com.example.sababukia_tbc.domain.usecase

import com.example.sababukia_tbc.domain.model.User
import com.example.sababukia_tbc.domain.repository.IAuthRepository
import javax.inject.Inject

class GetUsersUseCase @Inject constructor(
    private val repository: IAuthRepository
) {
    suspend operator fun invoke(page: Int = 1): Result<List<User>> {
        return repository.getUsers(page)
    }
}
