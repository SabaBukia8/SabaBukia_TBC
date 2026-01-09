package com.example.sababukia_tbc.presentation.screen.login

import androidx.lifecycle.viewModelScope
import com.example.sababukia_tbc.domain.common.ErrorType
import com.example.sababukia_tbc.domain.common.Resource
import com.example.sababukia_tbc.domain.repository.UserPreferencesRepository
import com.example.sababukia_tbc.domain.usecase.CheckSessionUseCase
import com.example.sababukia_tbc.domain.usecase.LoginUseCase
import com.example.sababukia_tbc.domain.usecase.SaveRememberMeUseCase
import com.example.sababukia_tbc.domain.usecase.ValidateEmailUseCase
import com.example.sababukia_tbc.domain.usecase.ValidatePasswordUseCase
import com.example.sababukia_tbc.domain.validator.ValidationResult
import com.example.sababukia_tbc.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val validateEmailUseCase: ValidateEmailUseCase,
    private val validatePasswordUseCase: ValidatePasswordUseCase,
    private val checkSessionUseCase: CheckSessionUseCase,
    private val saveRememberMeUseCase: SaveRememberMeUseCase,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val getPendingDeepLinkUseCase: com.example.sababukia_tbc.domain.usecase.GetPendingDeepLinkUseCase,
    private val clearPendingDeepLinkUseCase: com.example.sababukia_tbc.domain.usecase.ClearPendingDeepLinkUseCase
) : BaseViewModel<LoginState, LoginEvent, LoginSideEffect>(
    initialState = LoginState()
) {

    init {
        checkSession()
    }

    override fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.Login -> login(email = event.email, password = event.password)
            is LoginEvent.OnRegister -> onRegister()
            is LoginEvent.RememberMeChanged -> updateRememberMe(event.rememberMe)
        }
    }

    private fun checkSession() {
        viewModelScope.launch {
            val hasActiveSession = checkSessionUseCase()
            if (hasActiveSession) {
                // Check for pending deep link before navigating
                val pendingDeepLink = getPendingDeepLinkUseCase()
                if (!pendingDeepLink.isNullOrEmpty()) {
                    android.util.Log.d("LoginViewModel", "Auto-login with pending deep link: $pendingDeepLink")
                    clearPendingDeepLinkUseCase()
                    sendSideEffect(LoginSideEffect.NavigateToPendingDeepLink(pendingDeepLink))
                } else {
                    sendSideEffect(LoginSideEffect.NavigateToHome)
                }
            }
        }
    }

    private fun login(email: String, password: String) {
        val emailValidation = validateEmailUseCase(email)
        val passwordValidation = validatePasswordUseCase(password)

        if (emailValidation is ValidationResult.Invalid) {
            sendSideEffect(LoginSideEffect.ShowError(ErrorType.Validation.InvalidEmail))
            return
        }

        if (passwordValidation is ValidationResult.Invalid) {
            sendSideEffect(LoginSideEffect.ShowError(ErrorType.Validation.WeakPassword))
            return
        }

        collectResource(
            flow = loginUseCase(email, password),
            onLoading = { isLoading ->
                updateState { copy(loader = Resource.Loading(isLoading)) }
            },
            onError = { error ->
                updateState { copy(loader = Resource.Error(error)) }
                sendSideEffect(LoginSideEffect.ShowError(error))
            },
            onSuccess = { authResponse ->
                viewModelScope.launch {
                    userPreferencesRepository.saveAuthToken(authResponse.token)
                    userPreferencesRepository.saveEmail(email)
                    saveRememberMeUseCase(currentState.rememberMe)

                    updateState { copy(loader = Resource.Success(authResponse.token)) }

                    // Check for pending deep link after successful login
                    val pendingDeepLink = getPendingDeepLinkUseCase()
                    if (!pendingDeepLink.isNullOrEmpty()) {
                        android.util.Log.d("LoginViewModel", "Found pending deep link: $pendingDeepLink")
                        clearPendingDeepLinkUseCase()
                        sendSideEffect(LoginSideEffect.NavigateToPendingDeepLink(pendingDeepLink))
                    } else {
                        sendSideEffect(LoginSideEffect.NavigateToHome)
                    }
                }
            }
        )
    }

    private fun onRegister() {
        sendSideEffect(LoginSideEffect.NavigateToRegister)
    }

    private fun updateRememberMe(rememberMe: Boolean) {
        updateState { copy(rememberMe = rememberMe) }
    }
}
