package com.example.sababukia_tbc.presentation.screen.home

import com.example.sababukia_tbc.presentation.common.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
) : BaseViewModel<HomeViewModel.ViewState, HomeViewModel.Event, HomeViewModel.SideEffect>(
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