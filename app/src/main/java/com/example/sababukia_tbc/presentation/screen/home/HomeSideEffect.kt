package com.example.sababukia_tbc.presentation.screen.home

import com.example.sababukia_tbc.domain.common.ErrorType

sealed class HomeSideEffect {
    data object NavigateToProfile : HomeSideEffect()
    data object NavigateToUserProfile : HomeSideEffect()
    data class ShowError(val error: ErrorType) : HomeSideEffect()
}
