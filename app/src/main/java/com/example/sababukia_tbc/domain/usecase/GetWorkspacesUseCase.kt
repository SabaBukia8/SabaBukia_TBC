package com.example.sababukia_tbc.domain.usecase

import com.example.sababukia_tbc.domain.common.Resource
import com.example.sababukia_tbc.domain.model.WorkspaceItem
import com.example.sababukia_tbc.domain.repository.WorkspaceRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetWorkspacesUseCase @Inject constructor(
    private val repository: WorkspaceRepository
) {
    operator fun invoke(): Flow<Resource<List<WorkspaceItem>>> = repository.getWorkspaces()
}
