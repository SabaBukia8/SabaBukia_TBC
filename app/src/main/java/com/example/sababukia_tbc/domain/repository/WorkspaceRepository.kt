package com.example.sababukia_tbc.domain.repository

import com.example.sababukia_tbc.domain.common.Resource
import com.example.sababukia_tbc.domain.model.WorkspaceItem
import kotlinx.coroutines.flow.Flow

interface WorkspaceRepository {
    fun getWorkspaces(): Flow<Resource<List<WorkspaceItem>>>
}
