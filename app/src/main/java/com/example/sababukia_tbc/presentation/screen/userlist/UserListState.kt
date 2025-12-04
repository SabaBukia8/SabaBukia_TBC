package com.example.sababukia_tbc.presentation.screen.userlist

import com.example.sababukia_tbc.domain.model.UserListItem

data class UserListState(
    val users: List<UserListItem> = emptyList(),
    val isLoadingFromServer: Boolean = false,
    val isOnline: Boolean = true
)
