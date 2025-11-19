package com.example.sababukia_tbc.presentation.screen.register

sealed class RegisterSideEffect {
    data class NavigateBackToLogin(val email: String, val password: String) : RegisterSideEffect()
    object NavigateBack : RegisterSideEffect()
    data class ShowError(val errorMessage: String) : RegisterSideEffect()
}
