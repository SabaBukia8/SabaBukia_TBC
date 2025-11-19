package com.example.sababukia_tbc.presentation.screen.profile

sealed class ProfileEvent {
    object OnLogout : ProfileEvent()
    object OnBackPressed : ProfileEvent()
}
