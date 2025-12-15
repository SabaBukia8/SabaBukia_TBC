package com.example.sababukia_tbc.presentation.screen.favorites

import com.example.sababukia_tbc.presentation.common.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
) : BaseViewModel<FavoritesViewModel.ViewState, FavoritesViewModel.Event, FavoritesViewModel.SideEffect>(
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