package com.example.sababukia_tbc.presentation.screen.register

import androidx.lifecycle.viewModelScope
import com.example.sababukia_tbc.domain.common.ErrorType
import com.example.sababukia_tbc.domain.common.Resource
import com.example.sababukia_tbc.domain.usecase.RegisterUseCase
import com.example.sababukia_tbc.domain.usecase.ValidateEmailUseCase
import com.example.sababukia_tbc.domain.usecase.ValidatePasswordUseCase
import com.example.sababukia_tbc.domain.validator.ValidationResult
import com.example.sababukia_tbc.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase,
    private val validateEmailUseCase: ValidateEmailUseCase,
    private val validatePasswordUseCase: ValidatePasswordUseCase
) : BaseViewModel<RegisterState, RegisterEvent, RegisterSideEffect>(
    initialState = RegisterState()
) {

    override fun onEvent(event: RegisterEvent) {
        when (event) {
            is RegisterEvent.Register -> register(
                email = event.email,
                password = event.password,
                repeatPassword = event.repeatPassword
            )
            is RegisterEvent.OnBackPressed -> onBackPressed()
        }
    }

    private fun register(email: String, password: String, repeatPassword: String) {
        val emailValidation = validateEmailUseCase(email)
        val passwordValidation = validatePasswordUseCase(password)

        if (emailValidation is ValidationResult.Invalid) {
            sendSideEffect(RegisterSideEffect.ShowError(ErrorType.Validation.InvalidEmail))
            return
        }

        if (passwordValidation is ValidationResult.Invalid) {
            sendSideEffect(RegisterSideEffect.ShowError(ErrorType.Validation.WeakPassword))
            return
        }

        if (repeatPassword.isBlank()) {
            sendSideEffect(RegisterSideEffect.ShowError(ErrorType.Validation.EmptyField))
            return
        }

        if (password != repeatPassword) {
            sendSideEffect(RegisterSideEffect.ShowError(ErrorType.Validation.PasswordMismatch))
            return
        }

        collectResource(
            flow = registerUseCase(email, password),
            onLoading = { isLoading ->
                updateState { copy(loader = Resource.Loading(isLoading)) }
            },
            onError = { error ->
                updateState { copy(loader = Resource.Error(error)) }
                sendSideEffect(RegisterSideEffect.ShowError(error))
            },
            onSuccess = { authResponse ->
                viewModelScope.launch {
                    updateState { copy(loader = Resource.Success(authResponse.token)) }
                    sendSideEffect(
                        RegisterSideEffect.NavigateBackToLogin(
                            email = email,
                            password = password
                        )
                    )
                }
            }
        )
    }

    private fun onBackPressed() {
        sendSideEffect(RegisterSideEffect.NavigateBack)
    }
}
