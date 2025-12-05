package com.example.sababukia_tbc.data.repository

import com.example.sababukia_tbc.data.common.HandleResponse
import com.example.sababukia_tbc.data.mapper.toDomain
import com.example.sababukia_tbc.data.remote.api.WorkspaceApiService
import com.example.sababukia_tbc.domain.common.Resource
import com.example.sababukia_tbc.domain.model.WorkspaceItem
import com.example.sababukia_tbc.domain.repository.WorkspaceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorkspaceRepositoryImpl @Inject constructor(
    private val apiService: WorkspaceApiService,
    private val handleResponse: HandleResponse
) : WorkspaceRepository {

    override fun getWorkspaces(): Flow<Resource<List<WorkspaceItem>>> =
        handleResponse.safeApiCall { apiService.getWorkspaces() }
            .map { resource ->
                when (resource) {
                    is Resource.Success -> Resource.Success(resource.data.map { it.toDomain() })
                    is Resource.Error -> resource
                    is Resource.Loading -> resource
                }
            }
}
