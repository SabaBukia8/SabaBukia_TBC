package screen.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import domain.usecase.LoginUseCase
import domain.usecase.RegisterUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import repository.AuthResult
import util.ValidationUtil
import javax.inject.Inject

data class AuthUiState(
    val isLoading: Boolean = false,
    val isLoginSuccessful: Boolean = false,
    val isRegistrationSuccessful: Boolean = false,
    val errorMessage: String? = null,
    val validationErrors: List<String> = emptyList()
)

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

    private val _usernameText = MutableStateFlow("")
    val usernameText: StateFlow<String> = _usernameText.asStateFlow()

    private val _emailText = MutableStateFlow("")
    val emailText: StateFlow<String> = _emailText.asStateFlow()

    private val _passwordText = MutableStateFlow("")
    val passwordText: StateFlow<String> = _passwordText.asStateFlow()

    fun setUsername(username: String) {
        _usernameText.value = username
    }

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

            when (val result = loginUseCase(username, password)) {
                is AuthResult.Success -> {
                    Log.d(TAG, "Login successful")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isLoginSuccessful = true,
                        errorMessage = null
                    )
                }

                is AuthResult.Error -> {
                    Log.e(TAG, "Login failed: ${result.message}")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = result.message
                    )
                }

                is AuthResult.Loading -> {
                    // Handle loading state if needed
                }
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

            when (val result = registerUseCase(username, email, password)) {
                is AuthResult.Success -> {
                    Log.d(TAG, "Registration successful")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isRegistrationSuccessful = true,
                        errorMessage = null
                    )
                }

                is AuthResult.Error -> {
                    Log.e(TAG, "Registration failed: ${result.message}")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = result.message
                    )
                }

                is AuthResult.Loading -> {
                    // Handle loading state if needed
                }
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
