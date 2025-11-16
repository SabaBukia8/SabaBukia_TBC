package com.example.sababukia_tbc.presentation.ui.state


data class HomeUiState(
    val isLoading: Boolean = false,
    val username: String? = null,
    val email: String? = null,
    val userId: String? = null
)
