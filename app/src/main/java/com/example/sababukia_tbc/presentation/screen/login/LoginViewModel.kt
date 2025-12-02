package com.example.sababukia_tbc.presentation.screen.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sababukia_tbc.domain.common.Resource
import com.example.sababukia_tbc.domain.repository.UserPreferencesRepository
import com.example.sababukia_tbc.domain.usecase.CheckSessionUseCase
import com.example.sababukia_tbc.domain.usecase.LoginUseCase
import com.example.sababukia_tbc.domain.usecase.SaveRememberMeUseCase
import com.example.sababukia_tbc.domain.usecase.ValidateEmailUseCase
import com.example.sababukia_tbc.domain.usecase.ValidatePasswordUseCase
import com.example.sababukia_tbc.domain.validator.ValidationResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val validateEmailUseCase: ValidateEmailUseCase,
    private val validatePasswordUseCase: ValidatePasswordUseCase,
    private val checkSessionUseCase: CheckSessionUseCase,
    private val saveRememberMeUseCase: SaveRememberMeUseCase,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _state = MutableStateFlow(LoginState())
    val state = _state.asStateFlow()

    private val _sideEffect = MutableSharedFlow<LoginSideEffect>()
    val sideEffect = _sideEffect.asSharedFlow()

    private var loginJob: Job? = null

    init {
        checkSession()
    }

    fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.Login -> login(email = event.email, password = event.password)
            is LoginEvent.OnRegister -> onRegister()
        }
    }

    private fun checkSession() {
        viewModelScope.launch {
            val hasActiveSession = checkSessionUseCase()
            if (hasActiveSession) {
                _sideEffect.emit(LoginSideEffect.NavigateToHome)
            }
        }
    }

    private fun login(email: String, password: String) {
        // Validate inputs using dedicated use cases
        val emailValidation = validateEmailUseCase(email)
        val passwordValidation = validatePasswordUseCase(password)

        if (emailValidation is ValidationResult.Invalid) {
            viewModelScope.launch {
                _sideEffect.emit(LoginSideEffect.ShowError(emailValidation.errorMessage))
            }
            return
        }

        if (passwordValidation is ValidationResult.Invalid) {
            viewModelScope.launch {
                _sideEffect.emit(LoginSideEffect.ShowError(passwordValidation.errorMessage))
            }
            return
        }

        // All validations passed, proceed with login
        loginJob?.cancel()
        loginJob = viewModelScope.launch {
            loginUseCase(email, password).collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        _state.value = _state.value.copy(loader = resource)
                    }
                    is Resource.Success -> {
                        userPreferencesRepository.saveAuthToken(resource.data.token)
                        userPreferencesRepository.saveEmail(email)
                        saveRememberMeUseCase(_state.value.rememberMe)

                        _state.value = _state.value.copy(
                            loader = Resource.Success(data = resource.data.token)
                        )

                        _sideEffect.emit(LoginSideEffect.NavigateToHome)
                    }
                    is Resource.Error -> {
                        _state.value = _state.value.copy(loader = resource)
                        _sideEffect.emit(LoginSideEffect.ShowError(resource.errorMessage))
                    }
                }
            }
        }
    }

    private fun onRegister() {
        viewModelScope.launch {
            _sideEffect.emit(LoginSideEffect.NavigateToRegister)
        }
    }

    fun updateRememberMe(rememberMe: Boolean) {
        _state.value = _state.value.copy(rememberMe = rememberMe)
    }

    override fun onCleared() {
        super.onCleared()
        loginJob?.cancel()
    }
}
