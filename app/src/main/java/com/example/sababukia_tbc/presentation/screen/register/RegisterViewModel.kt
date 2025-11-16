package com.example.sababukia_tbc.presentation.screen.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sababukia_tbc.domain.usecase.RegisterUseCase
import com.example.sababukia_tbc.presentation.ui.navigation.NavigationEvent
import com.example.sababukia_tbc.presentation.ui.state.RegisterUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<NavigationEvent>()
    val navigationEvent: SharedFlow<NavigationEvent> = _navigationEvent.asSharedFlow()

    fun onEmailChanged(email: String) {
        _uiState.value = _uiState.value.copy(email = email, errorMessage = null)
        validateForm()
    }

    fun onPasswordChanged(password: String) {
        _uiState.value = _uiState.value.copy(password = password, errorMessage = null)
        validateForm()
    }

    fun onRepeatPasswordChanged(repeatPassword: String) {
        _uiState.value = _uiState.value.copy(repeatPassword = repeatPassword, errorMessage = null)
        validateForm()
    }

    fun onRegisterClicked() {
        viewModelScope.launch {
            if (_uiState.value.password != _uiState.value.repeatPassword) {
                _uiState.value = _uiState.value.copy(errorMessage = "Passwords do not match")
                return@launch
            }

            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            registerUseCase(_uiState.value.email, _uiState.value.password)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                    _navigationEvent.emit(
                        NavigationEvent.NavigateBackToLoginWithCredentials(
                            email = _uiState.value.email,
                            password = _uiState.value.password
                        )
                    )
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = exception.message ?: "Registration failed"
                    )
                }
        }
    }

    fun onBackClicked() {
        viewModelScope.launch {
            _navigationEvent.emit(NavigationEvent.NavigateBack)
        }
    }

    private fun validateForm() {
        val isValid = _uiState.value.email.isNotBlank() &&
                _uiState.value.email == "eve.holt@reqres.in" &&
                _uiState.value.password.isNotBlank() &&
                _uiState.value.repeatPassword.isNotBlank()
        _uiState.value = _uiState.value.copy(isRegisterButtonEnabled = isValid)
    }
}
