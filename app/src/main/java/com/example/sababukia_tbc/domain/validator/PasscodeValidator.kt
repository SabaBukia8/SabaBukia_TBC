package com.example.sababukia_tbc.domain.validator

object PasscodeValidator {
    private const val PASSCODE_LENGTH = 4

    fun validate(passcode: String): ValidationResult {
        return when {
            passcode.isBlank() -> ValidationResult.Invalid("Passcode cannot be empty")
            passcode.length != PASSCODE_LENGTH -> ValidationResult.Invalid("Passcode must be $PASSCODE_LENGTH digits")
            !passcode.all { it.isDigit() } -> ValidationResult.Invalid("Passcode must contain only digits")
            else -> ValidationResult.Valid
        }
    }
}
