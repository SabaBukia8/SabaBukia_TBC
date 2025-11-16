package com.example.sababukia_tbc.presentation.ui.state

data class AuthUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val validationErrors: List<String> = emptyList()
)
