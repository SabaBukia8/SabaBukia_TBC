package com.example.sababukia_tbc.presentation.screen.userlist

import com.example.sababukia_tbc.presentation.model.UserListItemUi

data class UserListState(
    val users: List<UserListItemUi> = emptyList(),
    val isLoadingFromServer: Boolean = false,
    val isOnline: Boolean = true
)
