package com.example.sababukia_tbc.presentation.screen.register

sealed class RegisterEvent {
    data class Register(val email: String, val password: String) : RegisterEvent()
    object OnBackPressed : RegisterEvent()
}
