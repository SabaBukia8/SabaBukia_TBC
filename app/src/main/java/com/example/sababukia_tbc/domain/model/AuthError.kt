package com.example.sababukia_tbc.domain.model

sealed class AuthError : DomainError {
    data object UserNotFound : AuthError()
    data object InvalidCredentials : AuthError()

    data object EmailAlreadyInUse : AuthError()

    data object UserIsNull : AuthError()
    data object UserNotSignedIn : AuthError()
    data object NetworkError : AuthError()

    data object NicknameUpdateFailed : AuthError()

    data class Unknown(val exception: Throwable? = null) : AuthError()
}
