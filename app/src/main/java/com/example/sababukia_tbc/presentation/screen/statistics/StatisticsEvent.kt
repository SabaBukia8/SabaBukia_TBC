package com.example.sababukia_tbc.presentation.screen.statistics

sealed interface StatisticsEvent {
    data object LoadWorkspaces : StatisticsEvent
}
