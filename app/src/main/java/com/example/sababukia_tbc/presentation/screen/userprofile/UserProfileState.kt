package com.example.sababukia_tbc.presentation.screen.userprofile

data class UserProfileState(
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val savedProfiles: List<UserProfileModel> = emptyList(),
    val isLoading: Boolean = false,
    val firstNameError: String? = null,
    val lastNameError: String? = null,
    val emailError: String? = null
)
