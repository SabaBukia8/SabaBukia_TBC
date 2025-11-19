package com.example.sababukia_tbc.presentation.screen.login

sealed class LoginSideEffect {
    object NavigateToHome : LoginSideEffect()
    object NavigateToRegister : LoginSideEffect()
    data class ShowError(val errorMessage: String) : LoginSideEffect()
}
