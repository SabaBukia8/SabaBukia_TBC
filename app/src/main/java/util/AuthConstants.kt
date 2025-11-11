package util

object AuthConstants {
    // ReqRes API specific email - this is the only email that works for registration
    const val VALID_EMAIL = "eve.holt@reqres.in"


    // Password constraints
    const val MIN_PASSWORD_LENGTH = 4

    // Validation error messages
    const val ERROR_EMPTY_EMAIL = "Email cannot be empty"
    const val ERROR_EMPTY_PASSWORD = "Password cannot be empty"
    const val ERROR_INVALID_EMAIL = "Please enter a valid email address"
    const val ERROR_INVALID_EMAIL_FOR_REGISTRATION =
        "Registration is only allowed with eve.holt@reqres.in"
    const val ERROR_SHORT_PASSWORD = "Password must be at least $MIN_PASSWORD_LENGTH characters"
}