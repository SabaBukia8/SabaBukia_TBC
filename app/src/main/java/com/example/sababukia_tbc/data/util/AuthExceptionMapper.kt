package com.example.sababukia_tbc.data.util

import com.example.sababukia_tbc.domain.model.AuthError
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException

fun mapAuthException(exception: Exception): AuthError = when (exception) {
    is FirebaseAuthInvalidUserException -> AuthError.UserNotFound
    is FirebaseAuthInvalidCredentialsException -> AuthError.InvalidCredentials

    is FirebaseAuthUserCollisionException -> AuthError.EmailAlreadyInUse

    is FirebaseNetworkException -> AuthError.NetworkError

    else -> AuthError.Unknown(exception)
}

fun mapProfileException(exception: Exception): AuthError = when (exception) {
    is FirebaseNetworkException -> AuthError.NetworkError
    else -> AuthError.NicknameUpdateFailed
}
