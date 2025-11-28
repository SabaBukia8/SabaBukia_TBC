package com.example.sababukia_tbc.presentation.screen.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sababukia_tbc.domain.common.Resource
import com.example.sababukia_tbc.domain.usecase.RegisterUseCase
import com.example.sababukia_tbc.presentation.common.ValidationUtils
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
            is RegisterEvent.Register -> register(
                email = event.email,
                password = event.password,
                repeatPassword = event.repeatPassword
            )
            is RegisterEvent.OnBackPressed -> onBackPressed()
        }
    }

    private fun register(email: String, password: String, repeatPassword: String) {
        // Validate inputs first
        val emailError = ValidationUtils.getEmailErrorMessage(email)
        val passwordError = ValidationUtils.getPasswordErrorMessage(password)
        val repeatPasswordError = when {
            repeatPassword.isBlank() -> "Please confirm your password"
            password != repeatPassword -> "Passwords do not match"
            else -> null
        }

        _state.value = _state.value.copy(
            emailError = emailError,
            passwordError = passwordError,
            repeatPasswordError = repeatPasswordError
        )

        if (emailError != null || passwordError != null || repeatPasswordError != null) {
            return
        }

        registerJob?.cancel()
        registerJob = viewModelScope.launch {
            registerUseCase(email, password).collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        _state.value = _state.value.copy(loader = resource)
                    }
                    is Resource.Success -> {
                        _state.value = _state.value.copy(
                            loader = Resource.Success(data = resource.data.token)
                        )

                        _sideEffect.emit(
                            RegisterSideEffect.NavigateBackToLogin(
                                email = email,
                                password = password
                            )
                        )
                    }
                    is Resource.Error -> {
                        _state.value = _state.value.copy(loader = resource)
                        _sideEffect.emit(RegisterSideEffect.ShowError(resource.errorMessage))
                    }
                }
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
