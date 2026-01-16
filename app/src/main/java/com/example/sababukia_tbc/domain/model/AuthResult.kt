package com.example.sababukia_tbc.domain.model


typealias AuthResult<T> = Result<T, AuthError>

fun <T> authSuccess(data: T): AuthResult<T> = Result.Success(data)

fun authError(error: AuthError): AuthResult<Nothing> = Result.Error(error)
