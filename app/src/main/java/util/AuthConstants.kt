package util

object AuthConstants {
    // ReqRes API specific email - this is the only email that works for registration
    const val VALID_EMAIL = "eve.holt@reqres.in"

    // For login, you can also use these test emails that exist in ReqRes:
    const val TEST_EMAIL_1 = "eve.holt@reqres.in"
    const val TEST_EMAIL_2 = "janet.weaver@reqres.in"
    const val TEST_EMAIL_3 = "emma.wong@reqres.in"

    // Password constraints
    const val MIN_PASSWORD_LENGTH = 4

    // Test passwords that work with ReqRes
    const val TEST_PASSWORD = "cityslicka"
    const val TEST_PASSWORD_2 = "pistol"

    // Validation error messages
    const val ERROR_EMPTY_EMAIL = "Email cannot be empty"
    const val ERROR_EMPTY_PASSWORD = "Password cannot be empty"
    const val ERROR_INVALID_EMAIL = "Please enter a valid email address"
    const val ERROR_INVALID_EMAIL_FOR_REGISTRATION =
        "Registration is only allowed with eve.holt@reqres.in"
    const val ERROR_SHORT_PASSWORD = "Password must be at least $MIN_PASSWORD_LENGTH characters"

    // Network error messages
    const val ERROR_NETWORK = "Network error occurred"
    const val ERROR_SERVER = "Server error occurred"
    const val ERROR_UNKNOWN = "Unknown error occurred"
}