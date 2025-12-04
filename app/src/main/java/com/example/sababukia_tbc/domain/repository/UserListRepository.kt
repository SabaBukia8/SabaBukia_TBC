package com.example.sababukia_tbc.domain.repository

import com.example.sababukia_tbc.domain.common.Resource
import com.example.sababukia_tbc.domain.model.UserListItem
import kotlinx.coroutines.flow.Flow

interface UserListRepository {
    fun getUsers(): Flow<List<UserListItem>>
    fun refreshUsers(): Flow<Resource<Unit>>
}
