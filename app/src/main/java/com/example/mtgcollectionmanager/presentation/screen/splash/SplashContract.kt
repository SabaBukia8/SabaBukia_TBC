package com.example.mtgcollectionmanager.presentation.screen.splash

object SplashContract {
    data class State(
        val isLoading: Boolean = true
    )

    sealed interface Event {
        data object CheckAuthStatus : Event
    }

    sealed interface SideEffect {
        data object NavigateToLogin : SideEffect
        data object NavigateToCollectionsList : SideEffect
    }
}
