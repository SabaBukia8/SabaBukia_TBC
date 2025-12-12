package com.example.mtgcollectionmanager.presentation.screen.auth.register

import androidx.lifecycle.viewModelScope
import com.example.mtgcollectionmanager.R
import com.example.mtgcollectionmanager.domain.common.Resource
import com.example.mtgcollectionmanager.domain.usecase.auth.RegisterUseCase
import com.example.mtgcollectionmanager.domain.usecase.collection.EnsureDefaultCollectionUseCase
import com.example.mtgcollectionmanager.presentation.common.BaseViewModel
import com.example.mtgcollectionmanager.presentation.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase,
    private val ensureDefaultCollectionUseCase: EnsureDefaultCollectionUseCase
) : BaseViewModel<RegisterContract.State, RegisterContract.Event, RegisterContract.SideEffect>(
    RegisterContract.State()
) {

    override fun onEvent(event: RegisterContract.Event) {
        when (event) {
            is RegisterContract.Event.EmailChanged -> {
                updateState { it.copy(email = event.email) }
            }
            is RegisterContract.Event.PasswordChanged -> {
                updateState { it.copy(password = event.password) }
            }
            is RegisterContract.Event.ConfirmPasswordChanged -> {
                updateState { it.copy(confirmPassword = event.confirmPassword) }
            }
            is RegisterContract.Event.RegisterClicked -> register()
            is RegisterContract.Event.LoginClicked -> {
                emitSideEffect(RegisterContract.SideEffect.NavigateToLogin)
            }
        }
    }

    private fun register() {
        viewModelScope.launch {
            with(state.value) {
                if (email.isBlank()) {
                    emitSideEffect(RegisterContract.SideEffect.ShowError(
                        UiText.StringResource(R.string.error_empty_email)
                    ))
                    return@launch
                }

                if (password.isBlank()) {
                    emitSideEffect(RegisterContract.SideEffect.ShowError(
                        UiText.StringResource(R.string.error_empty_password)
                    ))
                    return@launch
                }

                if (password.length < 6) {
                    emitSideEffect(RegisterContract.SideEffect.ShowError(
                        UiText.StringResource(R.string.error_password_too_short)
                    ))
                    return@launch
                }

                if (password != confirmPassword) {
                    emitSideEffect(RegisterContract.SideEffect.ShowError(
                        UiText.StringResource(R.string.error_password_mismatch)
                    ))
                    return@launch
                }

                registerUseCase(email, password).collect { resource ->
                    when (resource) {
                        is Resource.Loading -> {
                            updateState { it.copy(isLoading = resource.isLoading) }
                        }
                        is Resource.Success -> {
                            // Create default collection for new user
                            ensureDefaultCollectionUseCase().collect { collectionResource ->
                                when (collectionResource) {
                                    is Resource.Success -> {
                                        emitSideEffect(RegisterContract.SideEffect.NavigateToCollection)
                                    }
                                    is Resource.Error -> {
                                        // Still navigate even if default collection fails
                                        emitSideEffect(RegisterContract.SideEffect.NavigateToCollection)
                                    }
                                    is Resource.Loading -> {
                                        // Keep loading state
                                    }
                                }
                            }
                        }
                        is Resource.Error -> {
                            emitSideEffect(RegisterContract.SideEffect.ShowError(
                                UiText.DynamicString(resource.errorMessage)
                            ))
                        }
                    }
                }
            }
        }
    }
}
