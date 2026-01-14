package com.example.sababukia_tbc.presentation.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sababukia_tbc.domain.common.ErrorType
import com.example.sababukia_tbc.domain.common.Resource
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

abstract class BaseViewModel<State : Any, Event : Any, SideEffect : Any>(
    initialState: State
) : ViewModel() {

    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<State> = _state.asStateFlow()

    private val _sideEffect = Channel<SideEffect>(Channel.Factory.BUFFERED)
    val sideEffect: Flow<SideEffect> = _sideEffect.receiveAsFlow()

    protected val currentState: State
        get() = _state.value

    abstract fun onEvent(event: Event)

    protected fun updateState(reducer: State.() -> State) {
        _state.update(reducer)
    }

    protected fun sendSideEffect(effect: SideEffect) {
        viewModelScope.launch {
            _sideEffect.send(effect)
        }
    }

    protected fun <T> collectResource(
        flow: Flow<Resource<T>>,
        onLoading: (Boolean) -> Unit = {},
        onError: (ErrorType) -> Unit = {},
        onSuccess: (T) -> Unit
    ) {
        viewModelScope.launch {
            flow
                .catch { exception ->
                    onLoading(false)
                    onError(ErrorType.Generic(exception.message ?: "Unknown error"))
                }
                .collect { resource ->
                    when (resource) {
                        is Resource.Loading -> onLoading(resource.isLoading)
                        is Resource.Error -> onError(resource.error)
                        is Resource.Success -> onSuccess(resource.data)
                    }
                }
        }
    }
}