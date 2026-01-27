package com.example.sababukia_tbc.data.repository

import com.example.sababukia_tbc.data.common.safeCall
import com.example.sababukia_tbc.data.mapper.toDomain
import com.example.sababukia_tbc.data.remote.api.WorkspaceApiService
import com.example.sababukia_tbc.domain.model.Result
import com.example.sababukia_tbc.domain.model.WorkspaceError
import com.example.sababukia_tbc.domain.model.WorkspaceItem
import com.example.sababukia_tbc.domain.repository.WorkspaceRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorkspaceRepositoryImpl @Inject constructor(
    private val apiService: WorkspaceApiService
) : WorkspaceRepository {

    override suspend fun getWorkspaces(): Result<List<WorkspaceItem>, WorkspaceError> =
        safeCall {
            val response = apiService.getWorkspaces()
            if (response.isSuccessful) {
                response.body()?.map { it.toDomain() }
                    ?: throw Exception("Response body is null")
            } else {
                throw Exception(response.errorBody()?.string() ?: "Unknown error")
            }
        }
}
