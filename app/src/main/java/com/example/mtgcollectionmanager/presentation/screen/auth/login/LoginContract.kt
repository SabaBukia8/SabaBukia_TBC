package com.example.mtgcollectionmanager.presentation.screen.auth.login

import com.example.mtgcollectionmanager.presentation.util.UiText

object LoginContract {
    data class State(
        val isLoading: Boolean = false,
        val email: String = "",
        val password: String = ""
    )

    sealed interface Event {
        data class EmailChanged(val email: String) : Event
        data class PasswordChanged(val password: String) : Event
        data object LoginClicked : Event
        data object RegisterClicked : Event
    }

    sealed interface SideEffect {
        data object NavigateToRegister : SideEffect
        data object NavigateToCollection : SideEffect
        data class ShowError(val message: UiText) : SideEffect
    }
}
