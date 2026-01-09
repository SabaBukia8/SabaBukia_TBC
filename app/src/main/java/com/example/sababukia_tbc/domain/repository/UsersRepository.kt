package com.example.sababukia_tbc.domain.repository

import com.example.sababukia_tbc.domain.common.Resource
import com.example.sababukia_tbc.domain.model.PaginatedData
import com.example.sababukia_tbc.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UsersRepository {
    fun getUsers(page: Int): Flow<Resource<PaginatedData<User>>>
}
