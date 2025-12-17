package com.example.mtgcollectionmanager.presentation.screen.auth.login

import androidx.lifecycle.viewModelScope
import com.example.mtgcollectionmanager.R
import com.example.mtgcollectionmanager.domain.common.AppError
import com.example.mtgcollectionmanager.domain.common.Resource
import com.example.mtgcollectionmanager.presentation.util.toUiText
import com.example.mtgcollectionmanager.domain.usecase.auth.LoginUseCase
import com.example.mtgcollectionmanager.presentation.common.BaseViewModel
import com.example.mtgcollectionmanager.presentation.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
) : BaseViewModel<LoginContract.State, LoginContract.Event, LoginContract.SideEffect>(
    LoginContract.State()
) {

    override fun onEvent(event: LoginContract.Event) {
        when (event) {
            is LoginContract.Event.EmailChanged -> {
                updateState { it.copy(email = event.email) }
            }

            is LoginContract.Event.PasswordChanged -> {
                updateState { it.copy(password = event.password) }
            }

            is LoginContract.Event.LoginClicked -> login()
            is LoginContract.Event.RegisterClicked -> {
                emitSideEffect(LoginContract.SideEffect.NavigateToRegister)
            }
        }
    }

    private fun login() {
        viewModelScope.launch {
            with(state.value) {
                if (email.isBlank()) {
                    emitSideEffect(
                        LoginContract.SideEffect.ShowError(
                            UiText.StringResource(R.string.error_empty_email)
                        )
                    )
                    return@launch
                }

                if (password.isBlank()) {
                    emitSideEffect(
                        LoginContract.SideEffect.ShowError(
                            UiText.StringResource(R.string.error_empty_password)
                        )
                    )
                    return@launch
                }

                loginUseCase(email, password).collect { resource ->
                    when (resource) {
                        is Resource.Loading -> {
                            updateState { it.copy(isLoading = resource.isLoading) }
                        }

                        is Resource.Success -> {
                            emitSideEffect(LoginContract.SideEffect.NavigateToCollection)
                        }

                        is Resource.Error -> {
                            emitSideEffect(
                                LoginContract.SideEffect.ShowError(
                                    resource.error.toUiText()
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
