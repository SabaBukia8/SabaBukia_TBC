package com.example.sababukia_tbc.presentation.screen.login

import com.example.sababukia_tbc.domain.common.Resource

data class LoginState(
    val loader: Resource<String> = Resource.Loading(isLoading = false),
    val rememberMe: Boolean = false
)
