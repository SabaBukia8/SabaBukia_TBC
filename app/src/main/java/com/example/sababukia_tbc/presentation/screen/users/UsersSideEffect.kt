package com.example.sababukia_tbc.presentation.screen.users

sealed class UsersSideEffect {
    data class ShowUserDetails(val userId: Int) : UsersSideEffect()
}
