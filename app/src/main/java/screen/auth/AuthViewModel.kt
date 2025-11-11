package screen.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import repository.AuthRepository
import repository.AuthResult
import util.ValidationUtil

data class AuthUiState(
    val isLoading: Boolean = false,
    val isLoginSuccessful: Boolean = false,
    val isRegistrationSuccessful: Boolean = false,
    val errorMessage: String? = null,
    val validationErrors: List<String> = emptyList()
)

class AuthViewModel : ViewModel() {

    companion object {
        private const val TAG = "AuthViewModel"
    }

    private val authRepository = AuthRepository()

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private val _emailText = MutableStateFlow("")
    val emailText: StateFlow<String> = _emailText.asStateFlow()

    private val _passwordText = MutableStateFlow("")
    val passwordText: StateFlow<String> = _passwordText.asStateFlow()

    fun updateEmail(email: String) {
        _emailText.value = email
        clearValidationErrors()
    }

    fun updatePassword(password: String) {
        _passwordText.value = password
        clearValidationErrors()
    }

    fun login() {
        val email = _emailText.value.trim()
        val password = _passwordText.value

        Log.d(TAG, "Login attempt with email: $email")

        val validationErrors = ValidationUtil.validateForm(email, password, isRegistration = false)
        if (validationErrors.isNotEmpty()) {
            _uiState.value = _uiState.value.copy(
                validationErrors = validationErrors,
                errorMessage = null
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            when (val result = authRepository.login(email, password)) {
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
                    val errorMessage = when {
                        result.message.contains("missing", ignoreCase = true) &&
                                result.message.contains("api", ignoreCase = true) ->
                            "API Error: Please check network connection and API format"

                        result.message.contains("400") ->
                            "Invalid credentials. Try eve.holt@reqres.in with any password"

                        result.message.contains("404") ->
                            "User not found. Try eve.holt@reqres.in"

                        else -> result.message
                    }
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = errorMessage
                    )
                }
                is AuthResult.Loading -> {
                    // Handle loading state if needed
                }
            }
        }
    }

    fun register() {
        val email = _emailText.value.trim()
        val password = _passwordText.value

        Log.d(TAG, "Registration attempt with email: $email")

        val validationErrors = ValidationUtil.validateForm(email, password, isRegistration = true)
        if (validationErrors.isNotEmpty()) {
            _uiState.value = _uiState.value.copy(
                validationErrors = validationErrors,
                errorMessage = null
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            when (val result = authRepository.register(email, password)) {
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
                    val errorMessage = when {
                        result.message.contains("missing", ignoreCase = true) &&
                                result.message.contains("api", ignoreCase = true) ->
                            "API Error: Please check network connection and API format"

                        result.message.contains("400") ->
                            "Registration failed. Only eve.holt@reqres.in is allowed"

                        else -> result.message
                    }
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = errorMessage
                    )
                }
                is AuthResult.Loading -> {
                    // Handle loading state if needed
                }
            }
        }
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(
            errorMessage = null,
            isLoginSuccessful = false,
            isRegistrationSuccessful = false
        )
    }

    private fun clearValidationErrors() {
        if (_uiState.value.validationErrors.isNotEmpty()) {
            _uiState.value = _uiState.value.copy(validationErrors = emptyList())
        }
    }

    fun resetForm() {
        _emailText.value = ""
        _passwordText.value = ""
        _uiState.value = AuthUiState()
    }
}