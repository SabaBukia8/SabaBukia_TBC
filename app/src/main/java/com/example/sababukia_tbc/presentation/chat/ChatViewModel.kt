package com.example.sababukia_tbc.presentation.chat

import androidx.lifecycle.viewModelScope
import com.example.sababukia_tbc.domain.model.Chat
import com.example.sababukia_tbc.domain.model.ChatError
import com.example.sababukia_tbc.domain.repository.ChatRepository
import com.example.sababukia_tbc.presentation.common.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepository: ChatRepository
) : BaseViewModel<ChatState, ChatEvent, ChatSideEffect>(ChatState()) {

    init {
        loadChats()
    }

    override fun onEvent(event: ChatEvent) {
        when (event) {
            is ChatEvent.OnSearchQueryChanged -> updateSearchQuery(event.query)
            is ChatEvent.OnSearchClicked -> performSearch()
            is ChatEvent.OnRetry -> loadChats()
            is ChatEvent.DismissError -> dismissError()
        }
    }

    private fun loadChats() {
        viewModelScope.launch {
            updateState { copy(isLoading = true, error = null) }
            chatRepository.getChats()
                .onSuccess { chats ->
                    updateState {
                        copy(
                            chats = chats,
                            filteredChats = chats,
                            isLoading = false
                        )
                    }
                }
                .onError { error ->
                    updateState {
                        copy(
                            isLoading = false,
                            error = error.toMessage()
                        )
                    }
                }
        }
    }

    private fun updateSearchQuery(query: String) {
        updateState { copy(searchQuery = query) }
    }

    private fun performSearch() {
        val query = currentState.searchQuery.trim()
        val filtered = if (query.isEmpty()) {
            currentState.chats
        } else {
            currentState.chats.filter { chat ->
                chat.owner.contains(query, ignoreCase = true)
            }
        }
        updateState { copy(filteredChats = filtered) }
    }

    private fun dismissError() {
        updateState { copy(error = null) }
    }

    private fun ChatError.toMessage(): String = when (this) {
        ChatError.Network -> "Network error. Please check your connection."
        ChatError.Unknown -> "An unexpected error occurred."
    }
}

data class ChatState(
    val chats: List<Chat> = emptyList(),
    val filteredChats: List<Chat> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed interface ChatEvent {
    data class OnSearchQueryChanged(val query: String) : ChatEvent
    data object OnSearchClicked : ChatEvent
    data object OnRetry : ChatEvent
    data object DismissError : ChatEvent
}

sealed interface ChatSideEffect
