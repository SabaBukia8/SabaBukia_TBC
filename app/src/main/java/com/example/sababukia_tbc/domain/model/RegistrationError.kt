package com.example.sababukia_tbc.domain.model

sealed class RegistrationError : DomainError {
    data object Network : RegistrationError()
    data class Unknown(val message: String) : RegistrationError()
    data object ValidationFailed : RegistrationError()
    data object PinMismatch : RegistrationError()
}
