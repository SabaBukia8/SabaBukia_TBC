package com.example.sababukia_tbc.domain.model

sealed interface WorkspaceError : DomainError {
    data object Network : WorkspaceError
    data class Unknown(val message: String) : WorkspaceError
}
