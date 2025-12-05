package com.example.sababukia_tbc.presentation.screen.statistics

import com.example.sababukia_tbc.presentation.model.WorkspaceUiModel

data class StatisticsState(
    val workspaces: List<WorkspaceUiModel> = emptyList(),
    val isLoading: Boolean = false
)
