package com.example.sababukia_tbc.presentation.screen.userprofile

sealed class UserProfileEvent {
    data class OnFirstNameChanged(val firstName: String) : UserProfileEvent()
    data class OnLastNameChanged(val lastName: String) : UserProfileEvent()
    data class OnEmailChanged(val email: String) : UserProfileEvent()
    data object OnSaveClicked : UserProfileEvent()
    data object OnReadClicked : UserProfileEvent()
    data class OnDeleteProfile(val profileId: Long) : UserProfileEvent()
    data object OnClearForm : UserProfileEvent()
    data class LoadUser(val userId: String) : UserProfileEvent()
}
