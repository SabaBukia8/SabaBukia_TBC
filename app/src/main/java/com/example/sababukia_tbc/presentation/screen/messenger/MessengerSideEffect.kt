package com.example.sababukia_tbc.presentation.screen.messenger

sealed class MessengerSideEffect {
    data class ShowError(val errorMessage: String) : MessengerSideEffect()
    data class NavigateToChatDetails(val chatId: Int) : MessengerSideEffect()
}
