package com.example.sababukia_tbc.presentation.screen.passcode

data class PasscodeState(
    val passcodeDigits: List<String> = emptyList(),
    val isLoading: Boolean = false,
    val showSuccess: Boolean = false
)
