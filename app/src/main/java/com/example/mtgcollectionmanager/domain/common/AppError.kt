package com.example.mtgcollectionmanager.domain.common

sealed class AppError {

    sealed class Auth : AppError() {
        data object UserNotLoggedIn : Auth()
        data object LoginFailed : Auth()
        data object RegistrationFailed : Auth()
        data object InvalidCredentials : Auth()
        data object WeakPassword : Auth()
        data object EmailAlreadyInUse : Auth()
        data object PasswordChangeFailed : Auth()
    }

    sealed class Network : AppError() {
        data object NoConnection : Network()
        data object Timeout : Network()
        data object ServerError : Network()
    }

    sealed class Profile : AppError() {
        data object LoadFailed : Profile()
        data object UpdateFailed : Profile()
        data object DeleteFailed : Profile()
        data object NicknameUpdateFailed : Profile()
    }

    sealed class Collection : AppError() {
        data object LoadFailed : Collection()
        data object CreateFailed : Collection()
        data object UpdateFailed : Collection()
        data object DeleteFailed : Collection()
    }

    sealed class Card : AppError() {
        data object LoadFailed : Card()
        data object SearchFailed : Card()
        data object AddFailed : Card()
        data object RemoveFailed : Card()
        data object UpdateFailed : Card()
        data object NotFound : Card()
    }

    sealed class Category : AppError() {
        data object LoadFailed : Category()
        data object CreateFailed : Category()
        data object UpdateFailed : Category()
        data object DeleteFailed : Category()
        data object MoveCardFailed : Category()
    }

    data class Unknown(val message: String? = null) : AppError()
}