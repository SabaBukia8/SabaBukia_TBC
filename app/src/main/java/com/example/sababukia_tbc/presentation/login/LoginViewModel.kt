package com.example.sababukia_tbc.presentation.login

import androidx.lifecycle.viewModelScope
import com.example.sababukia_tbc.domain.model.AuthResult
import com.example.sababukia_tbc.domain.repository.LoginRepository
import com.example.sababukia_tbc.domain.util.ValidationError
import com.example.sababukia_tbc.domain.util.ValidationResult
import com.example.sababukia_tbc.domain.util.Validator
import com.example.sababukia_tbc.presentation.common.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginRepository: LoginRepository
) : BaseViewModel<LoginState, LoginEvent, LoginSideEffect>(LoginState()) {

    override fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.EmailChanged -> updateState {
                copy(email = event.email, emailError = null)
            }
            is LoginEvent.PasswordChanged -> updateState {
                copy(password = event.password, passwordError = null)
            }
            LoginEvent.Submit -> login()
        }
    }

    private fun login() {
        val emailValidation = Validator.validateEmail(currentState.email)
        val passwordValidation = Validator.validatePassword(currentState.password)

        val emailError = (emailValidation as? ValidationResult.Invalid)?.errorKey
        val passwordError = (passwordValidation as? ValidationResult.Invalid)?.errorKey

        if (emailError != null || passwordError != null) {
            updateState { copy(emailError = emailError, passwordError = passwordError) }
            return
        }

        viewModelScope.launch {
            updateState { copy(isLoading = true) }

            when (val result = loginRepository.login(currentState.email, currentState.password)) {
                is AuthResult.Success -> {
                    sendSideEffect(LoginSideEffect.ShowMessage(MessageType.SUCCESS))
                    sendSideEffect(LoginSideEffect.NavigateToHome)
                }
                is AuthResult.Error -> {
                    sendSideEffect(LoginSideEffect.ShowMessage(MessageType.ERROR, result.message))
                }
            }

            updateState { copy(isLoading = false) }
        }
    }
}

data class LoginState(
    val email: String = "",
    val password: String = "",
    val emailError: ValidationError? = null,
    val passwordError: ValidationError? = null,
    val isLoading: Boolean = false
)

sealed class LoginEvent {
    data class EmailChanged(val email: String) : LoginEvent()
    data class PasswordChanged(val password: String) : LoginEvent()
    data object Submit : LoginEvent()
}

sealed class LoginSideEffect {
    data object NavigateToHome : LoginSideEffect()
    data class ShowMessage(val type: MessageType, val customMessage: String? = null) : LoginSideEffect()
}

enum class MessageType {
    SUCCESS,
    ERROR
}
