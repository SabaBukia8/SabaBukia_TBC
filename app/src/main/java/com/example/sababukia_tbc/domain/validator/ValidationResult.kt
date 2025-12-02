package com.example.sababukia_tbc.domain.validator

sealed class ValidationResult {
    data object Valid : ValidationResult()
    data class Invalid(val errorMessage: String) : ValidationResult()
}
