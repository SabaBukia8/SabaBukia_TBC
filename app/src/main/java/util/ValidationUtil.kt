package util

import android.util.Patterns
import android.content.Context
import androidx.core.content.ContextCompat

object ValidationUtil {

    fun validateUsername(username: String): List<String> {
        val errors = mutableListOf<String>()
        when {
            username.isBlank() -> errors.add("error_username_required")
            username.length < 3 -> errors.add("error_username_min_length")
            username.length > 20 -> errors.add("error_username_max_length")
            !username.matches(Regex("^[a-zA-Z0-9_-]+$")) -> errors.add("error_username_invalid_format")
        }
        return errors
    }

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
        username: String,
        email: String,
        password: String,
        isRegistration: Boolean = false
    ): List<String> {
        val errors = mutableListOf<String>()

        val usernameErrors = validateUsername(username)
        errors.addAll(usernameErrors)

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
