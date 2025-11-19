package com.example.sababukia_tbc.presentation.screen.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sababukia_tbc.data.common.Resource
import com.example.sababukia_tbc.domain.usecase.RegisterUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(RegisterState())
    val state = _state.asStateFlow()

    private val _sideEffect = MutableSharedFlow<RegisterSideEffect>()
    val sideEffect = _sideEffect.asSharedFlow()

    private var registerJob: Job? = null

    fun onEvent(event: RegisterEvent) {
        when (event) {
            is RegisterEvent.Register -> register(email = event.email, password = event.password)
            is RegisterEvent.OnBackPressed -> onBackPressed()
        }
    }

    private fun register(email: String, password: String) {
        registerJob?.cancel()
        registerJob = viewModelScope.launch {
            _state.value = _state.value.copy(loader = Resource.Loading(isLoading = true))

            try {
                val result = registerUseCase(email, password)

                result
                    .onSuccess { authResponse ->
                        _state.value = _state.value.copy(
                            loader = Resource.Success(data = authResponse.token)
                        )

                        _sideEffect.emit(
                            RegisterSideEffect.NavigateBackToLogin(
                                email = email,
                                password = password
                            )
                        )
                    }
                    .onFailure { exception ->
                        val errorMessage = exception.message ?: "Registration failed"
                        _state.value = _state.value.copy(
                            loader = Resource.Error(errorMessage = errorMessage)
                        )
                        _sideEffect.emit(RegisterSideEffect.ShowError(errorMessage))
                    }
            } catch (e: Exception) {
                val errorMessage = e.message ?: "Unknown error"
                _state.value = _state.value.copy(
                    loader = Resource.Error(errorMessage = errorMessage)
                )
                _sideEffect.emit(RegisterSideEffect.ShowError(errorMessage))
            }
        }
    }

    private fun onBackPressed() {
        viewModelScope.launch {
            _sideEffect.emit(RegisterSideEffect.NavigateBack)
        }
    }

    override fun onCleared() {
        super.onCleared()
        registerJob?.cancel()
    }
}
