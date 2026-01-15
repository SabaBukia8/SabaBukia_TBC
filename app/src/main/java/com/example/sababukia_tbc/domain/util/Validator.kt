package com.example.sababukia_tbc.domain.util

object Validator {
    private val EMAIL_REGEX = Regex(
        "[a-zA-Z0-9+._%\\-]{1,256}" +
        "@" +
        "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
        "(" +
        "\\." +
        "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
        ")+"
    )

    private const val MIN_PASSWORD_LENGTH = 6

    fun validateEmail(email: String): ValidationResult {
        return when {
            email.isEmpty() -> ValidationResult.Invalid(ValidationError.EMPTY_EMAIL)
            !EMAIL_REGEX.matches(email) -> ValidationResult.Invalid(ValidationError.INVALID_EMAIL_FORMAT)
            else -> ValidationResult.Valid
        }
    }

    fun validatePassword(password: String): ValidationResult {
        return when {
            password.isEmpty() -> ValidationResult.Invalid(ValidationError.EMPTY_PASSWORD)
            password.length < MIN_PASSWORD_LENGTH -> ValidationResult.Invalid(ValidationError.PASSWORD_TOO_SHORT)
            else -> ValidationResult.Valid
        }
    }

    fun validateNickname(nickname: String): ValidationResult {
        return when {
            nickname.isEmpty() -> ValidationResult.Invalid(ValidationError.EMPTY_NICKNAME)
            else -> ValidationResult.Valid
        }
    }
}
