package com.example.sababukia_tbc.presentation.navigation

sealed class Routes(val route: String) {
    data object Home : Routes("home")
    data object Login : Routes("login")
    data object Register : Routes("register")
    data object RegisterNickname : Routes("register_nickname")
}
