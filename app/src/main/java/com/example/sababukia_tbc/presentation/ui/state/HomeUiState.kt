package com.example.sababukia_tbc.presentation.ui.state

import com.example.sababukia_tbc.domain.model.User

data class HomeUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val users: List<User> = emptyList()
)
