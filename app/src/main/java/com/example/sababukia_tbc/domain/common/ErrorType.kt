package com.example.sababukia_tbc.domain.common

sealed interface ErrorType {

    sealed interface Network : ErrorType {
        data object NoInternet : Network
        data object Timeout : Network
        data class Server(val code: Int, val message: String) : Network
        data class Unknown(val message: String) : Network
    }

    sealed interface Storage : ErrorType {
        data object UploadFailed : Storage
        data object NetworkUnavailable : Storage
        data class QuotaExceeded(val maxSize: Long) : Storage
        data class Unknown(val message: String) : Storage
    }

    sealed interface Permission : ErrorType {
        data object CameraDenied : Permission
        data object StorageDenied : Permission
        data object PermanentlyDenied : Permission
    }

    sealed interface Image : ErrorType {
        data object InvalidFormat : Image
        data class FileTooLarge(val maxSizeMb: Int) : Image
        data object CompressionFailed : Image
        data object NotSelected : Image
    }

    data class Generic(val message: String) : ErrorType
}
