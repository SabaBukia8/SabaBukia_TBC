package com.example.sababukia_tbc.domain.model

sealed class StoreError : DomainError {
    data object NetworkError : StoreError()
    data class Unknown(val exception: Throwable?) : StoreError()
}
