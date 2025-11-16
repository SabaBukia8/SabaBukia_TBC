package com.example.sababukia_tbc.presentation.screen.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sababukia_tbc.domain.usecase.CheckSessionUseCase
import com.example.sababukia_tbc.domain.usecase.LoginUseCase
import com.example.sababukia_tbc.domain.usecase.SaveRememberMeUseCase
import com.example.sababukia_tbc.domain.repository.IAuthRepository
import com.example.sababukia_tbc.presentation.ui.navigation.NavigationEvent
import com.example.sababukia_tbc.presentation.ui.state.LoginUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
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

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<NavigationEvent>()
    val navigationEvent: SharedFlow<NavigationEvent> = _navigationEvent.asSharedFlow()

    init {
        checkSession()
    }

    private fun checkSession() {
        viewModelScope.launch {
            val hasActiveSession = checkSessionUseCase()
            if (hasActiveSession) {
                _navigationEvent.emit(NavigationEvent.NavigateToHome)
            }
        }
    }

    fun onEmailChanged(email: String) {
        _uiState.value = _uiState.value.copy(email = email, errorMessage = null)
        validateForm()
    }

    fun onPasswordChanged(password: String) {
        _uiState.value = _uiState.value.copy(password = password, errorMessage = null)
        validateForm()
    }

    fun onRememberMeChanged(rememberMe: Boolean) {
        _uiState.value = _uiState.value.copy(rememberMe = rememberMe)
    }

    fun onLoginClicked() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            loginUseCase(_uiState.value.email, _uiState.value.password)
                .onSuccess { authResponse ->
                    repository.saveAuthToken(authResponse.token)
                    repository.saveEmail(_uiState.value.email)
                    saveRememberMeUseCase(_uiState.value.rememberMe)
                    _uiState.value = _uiState.value.copy(isLoading = false)
                    _navigationEvent.emit(NavigationEvent.NavigateToHome)
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = exception.message ?: "Login failed"
                    )
                }
        }
    }

    fun onRegisterClicked() {
        viewModelScope.launch {
            _navigationEvent.emit(NavigationEvent.NavigateToRegister)
        }
    }

    fun setCredentialsFromRegistration(email: String, password: String) {
        _uiState.value = _uiState.value.copy(email = email, password = password)
        validateForm()
    }

    private fun validateForm() {
        val isValid = _uiState.value.email.isNotBlank() &&
                _uiState.value.email == "eve.holt@reqres.in" &&
                _uiState.value.password.isNotBlank()
        _uiState.value = _uiState.value.copy(isLoginButtonEnabled = isValid)
    }
}
