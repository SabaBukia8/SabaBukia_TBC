package com.example.sababukia_tbc.presentation.screen.messages

import com.example.sababukia_tbc.presentation.common.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MessagesViewModel @Inject constructor(
) : BaseViewModel<MessagesViewModel.ViewState, MessagesViewModel.Event, MessagesViewModel.SideEffect>(
    initialState = ViewState()
) {

    data class ViewState(
        val isLoading: Boolean = false
    )

    sealed class Event {
    }

    sealed class SideEffect {
    }

    override fun onEvent(event: Event) {
    }
}