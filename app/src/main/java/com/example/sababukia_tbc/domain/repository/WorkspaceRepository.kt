package com.example.sababukia_tbc.domain.repository

import com.example.sababukia_tbc.domain.model.Result
import com.example.sababukia_tbc.domain.model.WorkspaceError
import com.example.sababukia_tbc.domain.model.WorkspaceItem

interface WorkspaceRepository {
    suspend fun getWorkspaces(): Result<List<WorkspaceItem>, WorkspaceError>
}
