package com.example.sababukia_tbc.domain.validator

object EmailValidator {

    private val EMAIL_REGEX = Regex(
        "[a-zA-Z0-9+._%\\-]{1,256}" +
        "@" +
        "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
        "(" +
        "\\." +
        "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
        ")+"
    )

    fun validate(email: String): ValidationResult {
        return when {
            email.isBlank() -> ValidationResult.Invalid("Email is required")
            !EMAIL_REGEX.matches(email) -> ValidationResult.Invalid("Please enter a valid email address")
            else -> ValidationResult.Valid
        }
    }
}
