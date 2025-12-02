package com.example.sababukia_tbc.domain.validator

object PasswordValidator {

    private const val MIN_PASSWORD_LENGTH = 6

    fun validate(password: String): ValidationResult {
        return when {
            password.isBlank() -> ValidationResult.Invalid("Password is required")
            password.length < MIN_PASSWORD_LENGTH -> ValidationResult.Invalid("Password must be at least $MIN_PASSWORD_LENGTH characters")
            else -> ValidationResult.Valid
        }
    }
}
