package com.example.sababukia_tbc.presentation.screen.home

sealed class HomeSideEffect {
    object NavigateToProfile : HomeSideEffect()
    data class ShowError(val errorMessage: String) : HomeSideEffect()
}
