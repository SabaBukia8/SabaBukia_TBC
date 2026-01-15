package com.example.sababukia_tbc.domain.util

sealed class ValidationResult {
    data object Valid : ValidationResult()
    data class Invalid(val errorKey: ValidationError) : ValidationResult()
}

enum class ValidationError {
    EMPTY_EMAIL,
    INVALID_EMAIL_FORMAT,
    EMPTY_PASSWORD,
    PASSWORD_TOO_SHORT,
    EMPTY_NICKNAME
}
