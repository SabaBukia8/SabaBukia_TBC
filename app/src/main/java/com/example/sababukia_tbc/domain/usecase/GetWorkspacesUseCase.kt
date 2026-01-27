package com.example.sababukia_tbc.domain.usecase

import com.example.sababukia_tbc.domain.model.Result
import com.example.sababukia_tbc.domain.model.WorkspaceError
import com.example.sababukia_tbc.domain.model.WorkspaceItem
import com.example.sababukia_tbc.domain.repository.WorkspaceRepository
import javax.inject.Inject

class GetWorkspacesUseCase @Inject constructor(
    private val repository: WorkspaceRepository
) {
    suspend operator fun invoke(): Result<List<WorkspaceItem>, WorkspaceError> =
        repository.getWorkspaces()
}
