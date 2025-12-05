package com.example.sababukia_tbc.presentation.screen.statistics

import com.example.sababukia_tbc.presentation.util.UiText

sealed interface StatisticsSideEffect {
    data class ShowError(val message: UiText) : StatisticsSideEffect
}
