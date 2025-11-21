package com.example.sababukia_tbc.presentation.screen.messenger

sealed class MessengerEvent {
    object LoadChats : MessengerEvent()
    data class SearchChats(val query: String) : MessengerEvent()
    data class OnChatClick(val chatId: Int) : MessengerEvent()
}
