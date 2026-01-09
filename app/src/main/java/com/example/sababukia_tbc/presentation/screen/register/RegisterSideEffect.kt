package com.example.sababukia_tbc.presentation.screen.register

import com.example.sababukia_tbc.domain.common.ErrorType

sealed class RegisterSideEffect {
    data class NavigateBackToLogin(val email: String, val password: String) : RegisterSideEffect()
    data object NavigateBack : RegisterSideEffect()
    data class ShowError(val error: ErrorType) : RegisterSideEffect()
}
