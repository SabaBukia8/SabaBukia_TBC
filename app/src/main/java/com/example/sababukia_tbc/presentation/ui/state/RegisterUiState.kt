package com.example.sababukia_tbc.presentation.ui.state

data class RegisterUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val email: String = "",
    val password: String = "",
    val repeatPassword: String = "",
    val isRegisterButtonEnabled: Boolean = false
)
