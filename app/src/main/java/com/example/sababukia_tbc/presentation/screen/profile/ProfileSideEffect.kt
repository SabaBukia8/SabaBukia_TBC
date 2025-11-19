package com.example.sababukia_tbc.presentation.screen.profile

sealed class ProfileSideEffect {
    object NavigateToLogin : ProfileSideEffect()
    object NavigateBack : ProfileSideEffect()
}
