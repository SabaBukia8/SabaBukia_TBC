package com.example.sababukia_tbc.presentation.util

import android.util.Patterns
import com.example.sababukia_tbc.R
import com.example.sababukia_tbc.data.remote.ApiConstants
import com.example.sababukia_tbc.domain.constants.ValidationConstants

object ValidationUtil {

    fun validateUsername(username: String): List<String> {
        val errors = mutableListOf<String>()
        when {
            username.isBlank() -> errors.add(StringResourceResolver.getString(R.string.error_username_required))
            username.length < ValidationConstants.MIN_USERNAME_LENGTH -> errors.add(StringResourceResolver.getString(R.string.error_username_min_length))
            username.length > ValidationConstants.MAX_USERNAME_LENGTH -> errors.add(StringResourceResolver.getString(R.string.error_username_max_length))
            !username.matches(Regex("^[a-zA-Z0-9_-]+$")) -> errors.add(StringResourceResolver.getString(R.string.error_username_invalid_format))
        }
        return errors
    }

    fun validateEmail(email: String): String? {
        return when {
            email.isBlank() -> StringResourceResolver.getString(R.string.error_empty_email)
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> StringResourceResolver.getString(R.string.error_invalid_email)
            else -> null
        }
    }

    fun validatePassword(password: String): String? {
        return when {
            password.isBlank() -> StringResourceResolver.getString(R.string.error_empty_password)
            password.length < ValidationConstants.MIN_PASSWORD_LENGTH -> StringResourceResolver.getString(R.string.error_password_min_length, ValidationConstants.MIN_PASSWORD_LENGTH)
            else -> null
        }
    }

    fun validateEmailForRegistration(email: String): String? {
        val basicEmailError = validateEmail(email)
        if (basicEmailError != null) return basicEmailError

        return if (email != ApiConstants.VALID_EMAIL) {
            StringResourceResolver.getString(R.string.error_invalid_email_for_registration, ApiConstants.VALID_EMAIL)
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
