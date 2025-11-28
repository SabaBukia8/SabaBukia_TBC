package com.example.sababukia_tbc.presentation.screen.register

import com.example.sababukia_tbc.domain.common.Resource

data class RegisterState(
    val loader: Resource<String> = Resource.Loading(isLoading = false),
    val emailError: String? = null,
    val passwordError: String? = null,
    val repeatPasswordError: String? = null
)
