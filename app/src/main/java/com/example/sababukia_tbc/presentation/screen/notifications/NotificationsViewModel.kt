package com.example.sababukia_tbc.presentation.screen.notifications

import com.example.sababukia_tbc.presentation.common.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NotificationsViewModel @Inject constructor(
) : BaseViewModel<NotificationsViewModel.ViewState, NotificationsViewModel.Event, NotificationsViewModel.SideEffect>(
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