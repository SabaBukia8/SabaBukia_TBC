package com.example.sababukia_tbc.presentation.common

import android.content.Context
import android.util.Patterns
import com.example.sababukia_tbc.R

object ValidationUtils {

    fun isValidName(name: String): Boolean {
        if (name.isBlank()) return false
        return name.matches(Regex("^[a-zA-Z\\s]+$"))
    }

    fun isValidEmail(email: String): Boolean {
        if (email.isBlank()) return false
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun isValidPassword(password: String): Boolean {
        return password.length >= 6
    }

    fun getNameErrorMessage(context: Context, fieldName: String, name: String): String? {
        return when {
            name.isBlank() -> context.getString(R.string.error_validation_field_required, fieldName)
            !isValidName(name) -> context.getString(R.string.error_validation_field_invalid, fieldName)
            else -> null
        }
    }

    fun getEmailErrorMessage(context: Context, email: String): String? {
        return when {
            email.isBlank() -> context.getString(R.string.error_validation_email_required)
            !isValidEmail(email) -> context.getString(R.string.error_validation_invalid_email)
            else -> null
        }
    }

    fun getPasswordErrorMessage(context: Context, password: String): String? {
        return when {
            password.isBlank() -> context.getString(R.string.error_validation_password_required)
            !isValidPassword(password) -> context.getString(R.string.error_validation_password_min_length)
            else -> null
        }
    }
}
