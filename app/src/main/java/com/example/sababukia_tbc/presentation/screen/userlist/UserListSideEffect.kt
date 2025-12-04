package com.example.sababukia_tbc.presentation.screen.userlist

import com.example.sababukia_tbc.presentation.util.UiText

sealed class UserListSideEffect {
    data class ShowError(val message: UiText) : UserListSideEffect()
}
