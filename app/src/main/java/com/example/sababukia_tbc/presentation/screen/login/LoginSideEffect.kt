package com.example.sababukia_tbc.presentation.screen.login

import com.example.sababukia_tbc.domain.common.ErrorType

sealed class LoginSideEffect {
    data object NavigateToHome : LoginSideEffect()
    data object NavigateToRegister : LoginSideEffect()
    data class ShowError(val error: ErrorType) : LoginSideEffect()
    data class NavigateToPendingDeepLink(val uri: String) : LoginSideEffect()
}
