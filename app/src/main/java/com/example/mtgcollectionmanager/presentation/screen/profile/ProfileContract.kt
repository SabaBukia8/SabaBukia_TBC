package com.example.mtgcollectionmanager.presentation.screen.profile

import com.example.mtgcollectionmanager.presentation.util.UiText

object ProfileContract {
    data class State(
        val isLoading: Boolean = false,
        val isNetworkAvailable: Boolean = true,
        val nickname: String = "",
        val email: String = "",
        val memberSince: String = "",
        val collectionCount: Int = 0,
        val totalCards: Int = 0,
        val isEditingNickname: Boolean = false
    )

    sealed interface Event {
        data object LoadProfile : Event
        data object ToggleEditNickname : Event
        data class UpdateNickname(val nickname: String) : Event
        data class SaveNickname(val nickname: String) : Event
        data object ChangePasswordClicked : Event
        data class ChangePassword(
            val currentPassword: String,
            val newPassword: String,
            val confirmPassword: String
        ) : Event

        data object DeleteAccountClicked : Event
        data class ConfirmDeleteAccount(val password: String) : Event
        data object LogoutClicked : Event
        data object ConfirmLogout : Event
    }

    sealed interface SideEffect {
        data class ShowError(val message: UiText) : SideEffect
        data class ShowSuccess(val message: UiText) : SideEffect
        data object ShowChangePasswordDialog : SideEffect
        data object ShowDeleteAccountDialog : SideEffect
        data object ShowLogoutConfirmation : SideEffect
        data object NavigateToLogin : SideEffect
    }
}
