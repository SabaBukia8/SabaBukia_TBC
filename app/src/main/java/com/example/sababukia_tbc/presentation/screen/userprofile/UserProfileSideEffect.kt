package com.example.sababukia_tbc.presentation.screen.userprofile

sealed class UserProfileSideEffect {
    data class ShowMessage(val message: String) : UserProfileSideEffect()
    data class ShowError(val error: String) : UserProfileSideEffect()
}
