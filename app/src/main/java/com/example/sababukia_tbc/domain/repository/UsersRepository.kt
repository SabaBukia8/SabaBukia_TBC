package com.example.sababukia_tbc.domain.repository

import androidx.paging.PagingData
import com.example.sababukia_tbc.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UsersRepository {
    fun getUsers(): Flow<PagingData<User>>
}
