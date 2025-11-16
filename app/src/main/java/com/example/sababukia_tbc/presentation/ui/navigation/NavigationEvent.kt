package com.example.sababukia_tbc.presentation.ui.navigation

sealed class NavigationEvent {
    object NavigateToHome : NavigationEvent()
    object NavigateToLogin : NavigationEvent()
    object NavigateToRegister : NavigationEvent()
    data class NavigateBackToLoginWithCredentials(val email: String, val password: String) : NavigationEvent()
    object NavigateBack : NavigationEvent()
}
