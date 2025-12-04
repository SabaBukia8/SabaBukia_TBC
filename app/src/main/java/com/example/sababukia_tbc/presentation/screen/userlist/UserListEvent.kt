package com.example.sababukia_tbc.presentation.screen.userlist

sealed class UserListEvent {
    data object Refresh : UserListEvent()
}
