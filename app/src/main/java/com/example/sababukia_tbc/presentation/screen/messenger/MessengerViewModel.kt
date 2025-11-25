package com.example.sababukia_tbc.presentation.screen.messenger

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sababukia_tbc.domain.common.Resource
import com.example.sababukia_tbc.domain.usecase.GetChatsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MessengerViewModel @Inject constructor(
    private val getChatsUseCase: GetChatsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(MessengerState())
    val state = _state.asStateFlow()

    private val _sideEffect = MutableSharedFlow<MessengerSideEffect>()
    val sideEffect = _sideEffect.asSharedFlow()

    private var searchJob: Job? = null

    init {
        onEvent(MessengerEvent.LoadChats)
    }

    fun onEvent(event: MessengerEvent) {
        when (event) {
            is MessengerEvent.LoadChats -> loadChats()
            is MessengerEvent.SearchChats -> searchChats(event.query)
            is MessengerEvent.OnChatClick -> onChatClick(event.chatId)
        }
    }

    private fun loadChats() {
        viewModelScope.launch {
            getChatsUseCase().collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        _state.update { it.copy(chatsResource = resource) }
                    }
                    is Resource.Success -> {
                        _state.update {
                            it.copy(
                                chatsResource = resource,
                                filteredChats = resource.data
                            )
                        }
                    }
                    is Resource.Error -> {
                        _state.update { it.copy(chatsResource = resource) }
                        _sideEffect.emit(MessengerSideEffect.ShowError(resource.errorMessage))
                    }
                }
            }
        }
    }

    private fun searchChats(query: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            _state.update { currentState ->
                val allChats = when (val resource = currentState.chatsResource) {
                    is Resource.Success -> resource.data
                    else -> emptyList()
                }

                val filtered = if (query.isBlank()) {
                    allChats
                } else {
                    allChats.filter { chat ->
                        chat.owner.contains(query, ignoreCase = true)
                    }
                }

                currentState.copy(
                    searchQuery = query,
                    filteredChats = filtered
                )
            }
        }
    }

    private fun onChatClick(chatId: Int) {
        viewModelScope.launch {
            _sideEffect.emit(MessengerSideEffect.NavigateToChatDetails(chatId))
        }
    }

    override fun onCleared() {
        super.onCleared()
        searchJob?.cancel()
    }
}
