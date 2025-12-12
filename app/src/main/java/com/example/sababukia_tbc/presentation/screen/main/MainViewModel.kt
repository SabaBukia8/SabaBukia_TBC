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
                android.util.Log.d("MainViewModel", "Network status change - Previous: $previousState, Current: $isConnected")

                updateState { it.copy(isConnected = isConnected) }

                if (previousState != isConnected) {
                    android.util.Log.d("MainViewModel", "State changed! Emitting side effect")
                    if (isConnected) {
                        emitSideEffect(SideEffect.ShowConnectedMessage)
                        android.util.Log.d("MainViewModel", "Emitted ShowConnectedMessage")
                    } else {
                        emitSideEffect(SideEffect.ShowDisconnectedMessage)
                        android.util.Log.d("MainViewModel", "Emitted ShowDisconnectedMessage")
                    }
                } else {
                    android.util.Log.d("MainViewModel", "State unchanged, no side effect emitted")
                }
            }
        }
    }
}
