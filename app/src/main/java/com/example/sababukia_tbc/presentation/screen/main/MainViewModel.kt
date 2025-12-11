package com.example.sababukia_tbc.presentation.screen.main

import androidx.lifecycle.viewModelScope
import com.example.sababukia_tbc.domain.repository.NetworkRepository
import com.example.sababukia_tbc.presentation.common.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val networkRepository: NetworkRepository
) : BaseViewModel<
    MainViewModel.State,
    MainViewModel.Event,
    MainViewModel.SideEffect
>(State()) {

    data class State(
        val isConnected: Boolean = true
    )

    sealed interface Event

    sealed interface SideEffect {
        data object ShowConnectedMessage : SideEffect
        data object ShowDisconnectedMessage : SideEffect
    }

    init {
        observeNetworkStatus()
    }

    private fun observeNetworkStatus() {
        viewModelScope.launch {
            networkRepository.isConnected.collect { isConnected ->
                val previousState = state.value.isConnected
                updateState { it.copy(isConnected = isConnected) }

                if (previousState != isConnected) {
                    if (isConnected) {
                        emitSideEffect(SideEffect.ShowConnectedMessage)
                    } else {
                        emitSideEffect(SideEffect.ShowDisconnectedMessage)
                    }
                }
            }
        }
    }
}
