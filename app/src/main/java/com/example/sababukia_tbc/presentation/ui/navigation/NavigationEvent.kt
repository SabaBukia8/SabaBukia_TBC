package com.example.sababukia_tbc.presentation.ui.navigation

sealed class NavigationEvent {
    object NavigateToHome : NavigationEvent()
    object NavigateToWelcome : NavigationEvent()
}
