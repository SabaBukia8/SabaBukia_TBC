package com.example.sababukia_tbc.domain.common

sealed interface ErrorType {

    sealed interface Network : ErrorType {
        data object NoInternet : Network
        data object Timeout : Network
        data class Server(val code: Int, val message: String) : Network
        data class Unknown(val message: String) : Network
    }

    sealed interface Auth : ErrorType {
        data object InvalidCredentials : Auth
        data object Unauthorized : Auth
        data object SessionExpired : Auth
    }

    sealed interface Validation : ErrorType {
        data object InvalidEmail : Validation
        data object WeakPassword : Validation
        data object PasswordMismatch : Validation
        data object EmptyField : Validation
    }

    data class Generic(val message: String) : ErrorType
}
