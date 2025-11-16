package com.example.sababukia_tbc.presentation.ui.state

data class LoginUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val email: String = "",
    val password: String = "",
    val rememberMe: Boolean = false,
    val isLoginButtonEnabled: Boolean = false
)
