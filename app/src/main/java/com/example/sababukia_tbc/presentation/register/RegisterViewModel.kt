package com.example.sababukia_tbc.presentation.register

import androidx.lifecycle.viewModelScope
import com.example.sababukia_tbc.domain.model.AuthResult
import com.example.sababukia_tbc.domain.repository.RegisterRepository
import com.example.sababukia_tbc.domain.util.ValidationError
import com.example.sababukia_tbc.domain.util.ValidationResult
import com.example.sababukia_tbc.domain.util.Validator
import com.example.sababukia_tbc.presentation.common.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerRepository: RegisterRepository
) : BaseViewModel<RegisterState, RegisterEvent, RegisterSideEffect>(RegisterState()) {

    override fun onEvent(event: RegisterEvent) {
        when (event) {
            is RegisterEvent.EmailChanged -> updateState {
                copy(email = event.email, emailError = null)
            }
            is RegisterEvent.PasswordChanged -> updateState {
                copy(password = event.password, passwordError = null)
            }
            RegisterEvent.Submit -> register()
        }
    }

    private fun register() {
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

            when (val result = registerRepository.register(currentState.email, currentState.password)) {
                is AuthResult.Success -> {
                    sendSideEffect(RegisterSideEffect.ShowMessage(MessageType.SUCCESS))
                    sendSideEffect(RegisterSideEffect.NavigateToNickname)
                }
                is AuthResult.Error -> {
                    sendSideEffect(RegisterSideEffect.ShowMessage(MessageType.ERROR, result.message))
                }
            }

            updateState { copy(isLoading = false) }
        }
    }
}

data class RegisterState(
    val email: String = "",
    val password: String = "",
    val emailError: ValidationError? = null,
    val passwordError: ValidationError? = null,
    val isLoading: Boolean = false
)

sealed class RegisterEvent {
    data class EmailChanged(val email: String) : RegisterEvent()
    data class PasswordChanged(val password: String) : RegisterEvent()
    data object Submit : RegisterEvent()
}

sealed class RegisterSideEffect {
    data object NavigateToNickname : RegisterSideEffect()
    data class ShowMessage(val type: MessageType, val customMessage: String? = null) : RegisterSideEffect()
}

enum class MessageType {
    SUCCESS,
    ERROR
}
