package com.example.sababukia_tbc.presentation.screen.userprofile

sealed class UserProfileEvent {
    data class OnFirstNameChanged(val firstName: String) : UserProfileEvent()
    data class OnLastNameChanged(val lastName: String) : UserProfileEvent()
    data class OnEmailChanged(val email: String) : UserProfileEvent()
    object OnSaveClicked : UserProfileEvent()
    object OnReadClicked : UserProfileEvent()
    data class OnDeleteProfile(val profileId: Long) : UserProfileEvent()
    object OnClearForm : UserProfileEvent()
}
