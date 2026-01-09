package com.example.sababukia_tbc.presentation.screen.login

sealed class LoginEvent {
    data class Login(val email: String, val password: String) : LoginEvent()
    data object OnRegister : LoginEvent()
    data class RememberMeChanged(val rememberMe: Boolean) : LoginEvent()
}
