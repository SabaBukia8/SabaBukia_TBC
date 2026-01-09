package com.example.sababukia_tbc.presentation.screen.register

sealed class RegisterEvent {
    data class Register(val email: String, val password: String, val repeatPassword: String) : RegisterEvent()
    data object OnBackPressed : RegisterEvent()
}
