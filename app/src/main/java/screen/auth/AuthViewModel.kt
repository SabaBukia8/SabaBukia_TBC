package screen.auth

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.sababukia_tbc.R
import data.DatastoreManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import model.LoginRequest
import model.RegisterRequest
import repository.AuthRepository
import repository.AuthResult
import util.ValidationUtil
import util.StringResourceResolver

data class AuthUiState(
    val isLoading: Boolean = false,
    val isLoginSuccessful: Boolean = false,
    val isRegistrationSuccessful: Boolean = false,
    val errorMessage: String? = null,
    val validationErrors: List<String> = emptyList()
)

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        private const val TAG = "AuthViewModel"
    }

    private val datastore = DatastoreManager(application.applicationContext)
    private val authRepository = AuthRepository()

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

    fun setOnboarded(value: Boolean) {
        viewModelScope.launch {
            datastore.setOnboarded(value)
        }
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

            try {
                // Check local credentials first (prioritize locally registered users)
                val savedUsername = datastore.registeredUsername.first()
                val savedPassword = datastore.registeredPassword.first()

                if (!savedUsername.isNullOrEmpty() && !savedPassword.isNullOrEmpty()) {
                    Log.d(TAG, "Found saved credentials: $savedUsername")

                    if (username.equals(savedUsername, ignoreCase = true)) {
                        if (savedPassword == password) {
                            Log.d(TAG, "Local login successful for: $username")
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                isLoginSuccessful = true,
                                errorMessage = null
                            )
                            return@launch
                        } else {
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                errorMessage = StringResourceResolver.getString(
                                    R.string.error_incorrect_password, 
                                    username
                                )
                            )
                            return@launch
                        }
                    }
                }

                // Fallback to API for test users or external accounts
                Log.d(TAG, "No local match found, trying API login")
                when (val result = authRepository.login(LoginRequest(username, password))) {
                    is AuthResult.Success -> {
                        Log.d(TAG, "API login successful")
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
            } catch (e: Exception) {
                Log.e(TAG, "Login error", e)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = StringResourceResolver.getString(
                        R.string.error_login_failed, 
                        e.message ?: "Unknown error"
                    )
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

            when (val result =
                authRepository.register(RegisterRequest(username, email, password))) {
                is AuthResult.Success -> {
                    Log.d(TAG, "Registration successful")
                    // Save credentials locally for future login enforcement
                    try {
                        datastore.saveRegisteredCredentials(username, email, password)
                        Log.d(TAG, "Saved credentials locally: $username")
                    } catch (_: Exception) {
                        Log.e(TAG, "Failed to save credentials locally")
                    }
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
