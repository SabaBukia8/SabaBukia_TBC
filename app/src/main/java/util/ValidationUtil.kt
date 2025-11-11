package util

import android.util.Patterns

object ValidationUtil {

    fun validateEmail(email: String): String? {
        return when {
            email.isBlank() -> AuthConstants.ERROR_EMPTY_EMAIL
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> AuthConstants.ERROR_INVALID_EMAIL
            else -> null
        }
    }

    fun validatePassword(password: String): String? {
        return when {
            password.isBlank() -> AuthConstants.ERROR_EMPTY_PASSWORD
            password.length < AuthConstants.MIN_PASSWORD_LENGTH -> AuthConstants.ERROR_SHORT_PASSWORD
            else -> null
        }
    }

    fun validateEmailForRegistration(email: String): String? {
        val basicEmailError = validateEmail(email)
        if (basicEmailError != null) return basicEmailError

        return if (email != AuthConstants.VALID_EMAIL) {
            AuthConstants.ERROR_INVALID_EMAIL_FOR_REGISTRATION
        } else {
            null
        }
    }

    fun validateForm(
        email: String,
        password: String,
        isRegistration: Boolean = false
    ): List<String> {
        val errors = mutableListOf<String>()

        val emailError = if (isRegistration) {
            validateEmailForRegistration(email)
        } else {
            validateEmail(email)
        }
        emailError?.let { errors.add(it) }

        val passwordError = validatePassword(password)
        passwordError?.let { errors.add(it) }

        return errors
    }
}