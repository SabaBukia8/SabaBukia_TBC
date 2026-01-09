package com.example.sababukia_tbc.presentation.screen.profile

sealed class ProfileSideEffect {
    data object NavigateToLogin : ProfileSideEffect()
    data object NavigateBack : ProfileSideEffect()
}
