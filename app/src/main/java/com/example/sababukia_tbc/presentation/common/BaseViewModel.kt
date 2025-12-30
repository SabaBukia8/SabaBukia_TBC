package com.example.sababukia_tbc.presentation.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sababukia_tbc.domain.common.Resource
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

abstract class BaseViewModel<State : Any, SideEffect : Any, Event : Any>(
    initialState: State
) : ViewModel() {

    private val _state = MutableStateFlow(initialState)
    val uiState: StateFlow<State> = _state.asStateFlow()

    private val _sideEffect = Channel<SideEffect>(Channel.BUFFERED)
    val sideEffect: Flow<SideEffect> = _sideEffect.receiveAsFlow()

    protected fun setState(reducer: State.() -> State) {
        _state.update { it.reducer() }
    }

    protected fun sendSideEffect(effect: SideEffect) {
        viewModelScope.launch {
            _sideEffect.send(effect)
        }
    }

    protected fun <T : Any> handleResponse(
        apiCall: () -> Flow<Resource<T>>,
        onSuccess: (T) -> Unit,
        onError: ((String) -> Unit)? = null,
        onLoading: (Resource.Loading) -> Unit,
    ) {
        viewModelScope.launch {
            apiCall.invoke().collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        onSuccess(resource.data)
                    }
                    is Resource.Error -> {
                        onError?.invoke(resource.message)
                    }
                    is Resource.Loading -> {
                        onLoading(resource)
                    }
                }
            }
        }
    }
}
