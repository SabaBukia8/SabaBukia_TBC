package com.example.sababukia_tbc.presentation.screen.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sababukia_tbc.data.common.Resource
import com.example.sababukia_tbc.domain.repository.IAuthRepository
import com.example.sababukia_tbc.domain.usecase.CheckSessionUseCase
import com.example.sababukia_tbc.domain.usecase.LoginUseCase
import com.example.sababukia_tbc.domain.usecase.SaveRememberMeUseCase
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
    private val checkSessionUseCase: CheckSessionUseCase,
    private val saveRememberMeUseCase: SaveRememberMeUseCase,
    private val repository: IAuthRepository
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
        loginJob?.cancel()
        loginJob = viewModelScope.launch {
            _state.value = _state.value.copy(loader = Resource.Loading(isLoading = true))

            try {
                val result = loginUseCase(email, password)

                result
                    .onSuccess { authResponse ->
                        repository.saveAuthToken(authResponse.token)
                        repository.saveEmail(email)
                        saveRememberMeUseCase(_state.value.rememberMe)

                        _state.value = _state.value.copy(
                            loader = Resource.Success(data = authResponse.token)
                        )

                        _sideEffect.emit(LoginSideEffect.NavigateToHome)
                    }
                    .onFailure { exception ->
                        val errorMessage = exception.message ?: "Login failed"
                        _state.value = _state.value.copy(
                            loader = Resource.Error(errorMessage = errorMessage)
                        )
                        _sideEffect.emit(LoginSideEffect.ShowError(errorMessage))
                    }
            } catch (e: Exception) {
                val errorMessage = e.message ?: "Unknown error"
                _state.value = _state.value.copy(
                    loader = Resource.Error(errorMessage = errorMessage)
                )
                _sideEffect.emit(LoginSideEffect.ShowError(errorMessage))
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
