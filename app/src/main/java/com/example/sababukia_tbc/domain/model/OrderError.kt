package com.example.sababukia_tbc.domain.model

sealed class OrderError : DomainError {
    data object Network : OrderError()
    data object Unknown : OrderError()
}
