package com.example.sababukia_tbc.domain.model


data class AuthResult(
    val token: String,
    val userId: Int? = null
)
