package com.example.sababukia_tbc.domain.common


sealed class DomainError {

    data object LocationServicesUnavailable : DomainError()


    data object LocationPermissionDenied : DomainError()


    data object DatabaseError : DomainError()

    data object ItemNotFound : DomainError()


    data object NetworkError : DomainError()


    data class GeneralError(val throwable: Throwable? = null) : DomainError()


    data object UnknownError : DomainError()
}