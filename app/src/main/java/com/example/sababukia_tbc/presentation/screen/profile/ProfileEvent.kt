package com.example.sababukia_tbc.presentation.screen.profile

sealed class ProfileEvent {
    data object OnLogout : ProfileEvent()
    data object OnBackPressed : ProfileEvent()
}
