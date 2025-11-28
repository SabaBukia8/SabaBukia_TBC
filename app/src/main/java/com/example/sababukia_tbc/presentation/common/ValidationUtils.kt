package com.example.sababukia_tbc.presentation.common

import android.util.Patterns

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

    fun getNameErrorMessage(fieldName: String, name: String): String? {
        return when {
            name.isBlank() -> "$fieldName is required"
            !isValidName(name) -> "$fieldName can only contain letters and spaces"
            else -> null
        }
    }

    fun getEmailErrorMessage(email: String): String? {
        return when {
            email.isBlank() -> "Email is required"
            !isValidEmail(email) -> "Please enter a valid email address"
            else -> null
        }
    }

    fun getPasswordErrorMessage(password: String): String? {
        return when {
            password.isBlank() -> "Password is required"
            !isValidPassword(password) -> "Password must be at least 6 characters"
            else -> null
        }
    }
}
