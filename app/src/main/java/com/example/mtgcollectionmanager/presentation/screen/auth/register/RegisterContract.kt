package com.example.mtgcollectionmanager.presentation.screen.auth.register

import com.example.mtgcollectionmanager.presentation.util.UiText

object RegisterContract {
    data class State(
        val isLoading: Boolean = false,
        val email: String = "",
        val password: String = "",
        val confirmPassword: String = ""
    )

    sealed interface Event {
        data class EmailChanged(val email: String) : Event
        data class PasswordChanged(val password: String) : Event
        data class ConfirmPasswordChanged(val confirmPassword: String) : Event
        data object RegisterClicked : Event
        data object LoginClicked : Event
    }

    sealed interface SideEffect {
        data object NavigateToLogin : SideEffect
        data object NavigateToCollection : SideEffect
        data class ShowError(val message: UiText) : SideEffect
    }
}
