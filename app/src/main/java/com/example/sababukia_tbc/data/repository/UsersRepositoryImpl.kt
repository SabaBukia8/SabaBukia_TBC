package com.example.sababukia_tbc.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.sababukia_tbc.data.remote.network.UsersApiService
import com.example.sababukia_tbc.data.remote.paging.UserPagingSource
import com.example.sababukia_tbc.domain.model.User
import com.example.sababukia_tbc.domain.repository.UsersRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UsersRepositoryImpl @Inject constructor(
    private val apiService: UsersApiService
) : UsersRepository {

    override fun getUsers(): Flow<PagingData<User>> {
        return Pager(
            config = PagingConfig(
                pageSize = 6,
                enablePlaceholders = false,
                initialLoadSize = 6
            ),
            pagingSourceFactory = { UserPagingSource(apiService) }
        ).flow
    }
}
