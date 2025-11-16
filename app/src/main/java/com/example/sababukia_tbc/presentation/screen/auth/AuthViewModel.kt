package com.example.sababukia_tbc.presentation.screen.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import com.example.sababukia_tbc.domain.usecase.LoginUseCase
import com.example.sababukia_tbc.domain.usecase.RegisterUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import com.example.sababukia_tbc.presentation.ui.state.AuthUiState
import com.example.sababukia_tbc.presentation.ui.navigation.NavigationEvent
import com.example.sababukia_tbc.presentation.util.ValidationUtil
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase
) : ViewModel() {

    companion object {
        private const val TAG = "AuthViewModel"
    }

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<NavigationEvent>()
    val navigationEvent: SharedFlow<NavigationEvent> = _navigationEvent.asSharedFlow()

    private val _usernameText = MutableStateFlow("")
    val usernameText: StateFlow<String> = _usernameText.asStateFlow()

    private val _emailText = MutableStateFlow("")
    val emailText: StateFlow<String> = _emailText.asStateFlow()

    private val _passwordText = MutableStateFlow("")
    val passwordText: StateFlow<String> = _passwordText.asStateFlow()

    fun updateUsername(username: String) {
        _usernameText.value = username
        clearValidationErrors()
    }

    fun updateEmail(email: String) {
        _emailText.value = email
        clearValidationErrors()
    }

    fun updatePassword(password: String) {
        _passwordText.value = password
        clearValidationErrors()
    }

    fun login() {
        val username = _usernameText.value.trim()
        val password = _passwordText.value

        Log.d(TAG, "Login attempt with username: $username")

        val validationErrors = ValidationUtil.validateUsername(username)
        if (validationErrors.isNotEmpty()) {
            _uiState.value = _uiState.value.copy(
                validationErrors = validationErrors,
                errorMessage = null
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            loginUseCase(username, password)
                .onSuccess {
                    Log.d(TAG, "Login successful")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = null
                    )
                    _navigationEvent.emit(NavigationEvent.NavigateToHome)
                }
                .onFailure { exception ->
                    Log.e(TAG, "Login failed: ${exception.message}")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = exception.message
                    )
                }
        }
    }

    fun register() {
        val username = _usernameText.value.trim()
        val email = _emailText.value.trim()
        val password = _passwordText.value

        Log.d(TAG, "Registration attempt with username: $username, email: $email")

        val validationErrors =
            ValidationUtil.validateForm(username, email, password, isRegistration = true)
        if (validationErrors.isNotEmpty()) {
            _uiState.value = _uiState.value.copy(
                validationErrors = validationErrors,
                errorMessage = null
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            registerUseCase(username, email, password)
                .onSuccess {
                    Log.d(TAG, "Registration successful")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = null
                    )
                    _navigationEvent.emit(NavigationEvent.NavigateToHome)
                }
                .onFailure { exception ->
                    Log.e(TAG, "Registration failed: ${exception.message}")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = exception.message
                    )
                }
        }
    }

    private fun clearValidationErrors() {
        if (_uiState.value.validationErrors.isNotEmpty()) {
            _uiState.value = _uiState.value.copy(validationErrors = emptyList())
        }
    }

    fun resetForm() {
        _usernameText.value = ""
        _emailText.value = ""
        _passwordText.value = ""
        _uiState.value = AuthUiState()
    }
}
