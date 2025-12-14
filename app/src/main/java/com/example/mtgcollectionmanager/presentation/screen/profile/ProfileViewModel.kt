package com.example.mtgcollectionmanager.presentation.screen.profile

import androidx.lifecycle.viewModelScope
import com.example.mtgcollectionmanager.R
import com.example.mtgcollectionmanager.domain.common.Resource
import com.example.mtgcollectionmanager.domain.usecase.auth.ChangePasswordUseCase
import com.example.mtgcollectionmanager.domain.usecase.auth.DeleteAccountUseCase
import com.example.mtgcollectionmanager.domain.usecase.auth.GetUserProfileUseCase
import com.example.mtgcollectionmanager.domain.usecase.auth.LogoutUseCase
import com.example.mtgcollectionmanager.domain.usecase.auth.UpdateNicknameUseCase
import com.example.mtgcollectionmanager.presentation.common.BaseViewModel
import com.example.mtgcollectionmanager.presentation.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val updateNicknameUseCase: UpdateNicknameUseCase,
    private val changePasswordUseCase: ChangePasswordUseCase,
    private val deleteAccountUseCase: DeleteAccountUseCase,
    private val logoutUseCase: LogoutUseCase
) : BaseViewModel<ProfileContract.State, ProfileContract.Event, ProfileContract.SideEffect>(
    ProfileContract.State()
) {

    init {
        loadProfile()
    }

    override fun onEvent(event: ProfileContract.Event) {
        when (event) {
            ProfileContract.Event.LoadProfile -> loadProfile()
            ProfileContract.Event.ToggleEditNickname -> {
                updateState { it.copy(isEditingNickname = !it.isEditingNickname) }
            }

            is ProfileContract.Event.UpdateNickname -> {
                updateState { it.copy(nickname = event.nickname) }
            }

            is ProfileContract.Event.SaveNickname -> saveNickname(event.nickname)
            ProfileContract.Event.ChangePasswordClicked -> {
                emitSideEffect(ProfileContract.SideEffect.ShowChangePasswordDialog)
            }

            is ProfileContract.Event.ChangePassword -> {
                changePassword(event.currentPassword, event.newPassword, event.confirmPassword)
            }

            ProfileContract.Event.DeleteAccountClicked -> {
                emitSideEffect(ProfileContract.SideEffect.ShowDeleteAccountDialog)
            }

            is ProfileContract.Event.ConfirmDeleteAccount -> {
                deleteAccount(event.password)
            }

            ProfileContract.Event.LogoutClicked -> {
                emitSideEffect(ProfileContract.SideEffect.ShowLogoutConfirmation)
            }

            ProfileContract.Event.ConfirmLogout -> logout()
        }
    }

    private fun loadProfile() {
        viewModelScope.launch {
            getUserProfileUseCase().collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        updateState { it.copy(isLoading = resource.isLoading) }
                    }

                    is Resource.Success -> {
                        val profile = resource.data
                        val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                        val memberSince = dateFormat.format(Date(profile.createdAt))

                        updateState {
                            it.copy(
                                nickname = profile.nickname,
                                email = profile.email,
                                memberSince = memberSince,
                                collectionCount = profile.collectionCount,
                                totalCards = profile.totalCards
                            )
                        }
                    }

                    is Resource.Error -> {
                        emitSideEffect(
                            ProfileContract.SideEffect.ShowError(
                                UiText.DynamicString(resource.errorMessage)
                            )
                        )
                    }
                }
            }
        }
    }

    private fun saveNickname(nickname: String) {
        if (nickname.isBlank()) {
            emitSideEffect(
                ProfileContract.SideEffect.ShowError(
                    UiText.StringResource(R.string.error_generic)
                )
            )
            return
        }

        viewModelScope.launch {
            updateNicknameUseCase(nickname).collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        updateState { it.copy(isLoading = resource.isLoading) }
                    }

                    is Resource.Success -> {
                        // Update nickname in state immediately to show the change
                        updateState {
                            it.copy(
                                nickname = nickname,
                                isEditingNickname = false
                            )
                        }
                        emitSideEffect(
                            ProfileContract.SideEffect.ShowSuccess(
                                UiText.StringResource(R.string.nickname_updated_success)
                            )
                        )
                    }

                    is Resource.Error -> {
                        emitSideEffect(
                            ProfileContract.SideEffect.ShowError(
                                UiText.DynamicString(resource.errorMessage)
                            )
                        )
                    }
                }
            }
        }
    }

    private fun changePassword(
        currentPassword: String,
        newPassword: String,
        confirmPassword: String
    ) {
        when {
            currentPassword.isBlank() -> {
                emitSideEffect(
                    ProfileContract.SideEffect.ShowError(
                        UiText.StringResource(R.string.error_empty_password)
                    )
                )
                return
            }

            newPassword.isBlank() -> {
                emitSideEffect(
                    ProfileContract.SideEffect.ShowError(
                        UiText.StringResource(R.string.error_empty_password)
                    )
                )
                return
            }

            newPassword.length < 6 -> {
                emitSideEffect(
                    ProfileContract.SideEffect.ShowError(
                        UiText.StringResource(R.string.error_password_too_short)
                    )
                )
                return
            }

            newPassword != confirmPassword -> {
                emitSideEffect(
                    ProfileContract.SideEffect.ShowError(
                        UiText.StringResource(R.string.error_password_mismatch)
                    )
                )
                return
            }
        }

        viewModelScope.launch {
            changePasswordUseCase(currentPassword, newPassword).collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        updateState { it.copy(isLoading = resource.isLoading) }
                    }

                    is Resource.Success -> {
                        emitSideEffect(
                            ProfileContract.SideEffect.ShowSuccess(
                                UiText.StringResource(R.string.password_changed_success)
                            )
                        )
                    }

                    is Resource.Error -> {
                        emitSideEffect(
                            ProfileContract.SideEffect.ShowError(
                                UiText.DynamicString(resource.errorMessage)
                            )
                        )
                    }
                }
            }
        }
    }

    private fun deleteAccount(password: String) {
        if (password.isBlank()) {
            emitSideEffect(
                ProfileContract.SideEffect.ShowError(
                    UiText.StringResource(R.string.error_empty_password)
                )
            )
            return
        }

        viewModelScope.launch {
            deleteAccountUseCase(password).collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        updateState { it.copy(isLoading = resource.isLoading) }
                    }

                    is Resource.Success -> {
                        emitSideEffect(
                            ProfileContract.SideEffect.ShowSuccess(
                                UiText.StringResource(R.string.account_deleted_success)
                            )
                        )
                        emitSideEffect(ProfileContract.SideEffect.NavigateToLogin)
                    }

                    is Resource.Error -> {
                        emitSideEffect(
                            ProfileContract.SideEffect.ShowError(
                                UiText.DynamicString(resource.errorMessage)
                            )
                        )
                    }
                }
            }
        }
    }

    private fun logout() {
        logoutUseCase()
        emitSideEffect(ProfileContract.SideEffect.NavigateToLogin)
    }
}
