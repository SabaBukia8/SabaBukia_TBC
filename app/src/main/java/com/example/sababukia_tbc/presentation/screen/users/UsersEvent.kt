package com.example.sababukia_tbc.presentation.screen.users

sealed class UsersEvent {
    data class OnUserClick(val userId: Int) : UsersEvent()
}
