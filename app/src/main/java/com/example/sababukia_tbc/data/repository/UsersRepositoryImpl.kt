package com.example.sababukia_tbc.data.repository

import com.example.sababukia_tbc.data.common.HandleResponse
import com.example.sababukia_tbc.data.mapper.toDomain
import com.example.sababukia_tbc.data.model.remote.network.UsersApiService
import com.example.sababukia_tbc.domain.common.Resource
import com.example.sababukia_tbc.domain.common.mapResource
import com.example.sababukia_tbc.domain.model.PaginatedData
import com.example.sababukia_tbc.domain.model.User
import com.example.sababukia_tbc.domain.repository.UsersRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UsersRepositoryImpl @Inject constructor(
    private val apiService: UsersApiService,
    private val handleResponse: HandleResponse
) : UsersRepository {

    override fun getUsers(page: Int): Flow<Resource<PaginatedData<User>>> {
        return handleResponse.safeApiCall {
            apiService.getUsers(page)
        }.mapResource { usersResponse ->
            PaginatedData(
                items = usersResponse.data.toDomain(),
                currentPage = usersResponse.page,
                totalPages = usersResponse.totalPages,
                total = usersResponse.total,
                hasNextPage = usersResponse.page < usersResponse.totalPages
            )
        }
    }
}
