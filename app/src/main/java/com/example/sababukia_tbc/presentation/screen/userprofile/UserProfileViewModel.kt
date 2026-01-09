package com.example.sababukia_tbc.presentation.screen.userprofile

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.example.sababukia_tbc.domain.repository.UserProfileRepository
import com.example.sababukia_tbc.presentation.base.BaseViewModel
import com.example.sababukia_tbc.presentation.common.ValidationUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserProfileViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val userProfileRepository: UserProfileRepository
) : BaseViewModel<UserProfileState, UserProfileEvent, UserProfileSideEffect>(
    initialState = UserProfileState()
) {

    override fun onEvent(event: UserProfileEvent) {
        when (event) {
            is UserProfileEvent.OnFirstNameChanged -> updateFirstName(event.firstName)
            is UserProfileEvent.OnLastNameChanged -> updateLastName(event.lastName)
            is UserProfileEvent.OnEmailChanged -> updateEmail(event.email)
            is UserProfileEvent.OnSaveClicked -> saveUserProfile()
            is UserProfileEvent.OnReadClicked -> loadProfiles()
            is UserProfileEvent.OnDeleteProfile -> deleteProfile(event.profileId)
            is UserProfileEvent.OnClearForm -> clearForm()
            is UserProfileEvent.LoadUser -> loadUserFromApi(event.userId)
        }
    }

    private fun loadUserFromApi(userId: String) {
        // TODO: Implement API call to fetch user by ID
        // For now, just log the userId to verify deep link works
        android.util.Log.d("UserProfileViewModel", "Load user from API: $userId")
    }

    private fun updateFirstName(firstName: String) {
        updateState {
            copy(
                firstName = firstName,
                firstNameError = null
            )
        }
    }

    private fun updateLastName(lastName: String) {
        updateState {
            copy(
                lastName = lastName,
                lastNameError = null
            )
        }
    }

    private fun updateEmail(email: String) {
        updateState {
            copy(
                email = email,
                emailError = null
            )
        }
    }

    private fun validateInputs(): Boolean {
        var isValid = true

        val firstNameError = ValidationUtils.getNameErrorMessage(context, "First name", currentState.firstName)
        val lastNameError = ValidationUtils.getNameErrorMessage(context, "Last name", currentState.lastName)
        val emailError = ValidationUtils.getEmailErrorMessage(context, currentState.email)

        if (firstNameError != null || lastNameError != null || emailError != null) {
            updateState {
                copy(
                    firstNameError = firstNameError,
                    lastNameError = lastNameError,
                    emailError = emailError
                )
            }
            isValid = false
        }

        return isValid
    }

    private suspend fun checkEmailDuplicate(email: String, excludeId: Long? = null): Boolean {
        return userProfileRepository.checkEmailExists(email, excludeId)
    }

    private fun saveUserProfile() {
        viewModelScope.launch {
            try {
                if (!validateInputs()) {
                    return@launch
                }

                val state = currentState

                if (checkEmailDuplicate(state.email)) {
                    updateState {
                        copy(emailError = "This email is already used")
                    }
                    sendSideEffect(
                        UserProfileSideEffect.ShowError("This email is already registered")
                    )
                    return@launch
                }

                updateState { copy(isLoading = true) }

                userProfileRepository.saveProfile(
                    firstName = state.firstName,
                    lastName = state.lastName,
                    email = state.email
                )

                clearForm()
                updateState { copy(isLoading = false) }

                sendSideEffect(
                    UserProfileSideEffect.ShowMessage("Profile saved successfully! Click 'Read' to view all profiles.")
                )
            } catch (e: Exception) {
                updateState { copy(isLoading = false) }
                sendSideEffect(
                    UserProfileSideEffect.ShowError("Failed to save profile: ${e.message}")
                )
            }
        }
    }

    private fun loadProfiles() {
        viewModelScope.launch {
            try {
                updateState { copy(isLoading = true) }

                val domainProfiles = userProfileRepository.getProfiles().first()

                val profileModels = domainProfiles.map { profile ->
                    UserProfileModel(
                        id = profile.id,
                        firstName = profile.firstName,
                        lastName = profile.lastName,
                        email = profile.email
                    )
                }

                updateState {
                    copy(
                        savedProfiles = profileModels,
                        isLoading = false
                    )
                }

                if (profileModels.isNotEmpty()) {
                    sendSideEffect(
                        UserProfileSideEffect.ShowMessage("${profileModels.size} profile(s) loaded")
                    )
                } else {
                    sendSideEffect(
                        UserProfileSideEffect.ShowMessage("No saved profiles found")
                    )
                }
            } catch (e: Exception) {
                updateState { copy(isLoading = false) }
                sendSideEffect(
                    UserProfileSideEffect.ShowError("Failed to load profiles: ${e.message}")
                )
            }
        }
    }

    private fun deleteProfile(profileId: Long) {
        viewModelScope.launch {
            try {
                updateState { copy(isLoading = true) }

                userProfileRepository.deleteProfile(profileId)

                val updatedProfiles = currentState.savedProfiles.filter { it.id != profileId }
                updateState {
                    copy(
                        savedProfiles = updatedProfiles,
                        isLoading = false
                    )
                }

                sendSideEffect(
                    UserProfileSideEffect.ShowMessage("Profile deleted successfully")
                )
            } catch (e: Exception) {
                updateState { copy(isLoading = false) }
                sendSideEffect(
                    UserProfileSideEffect.ShowError("Failed to delete profile: ${e.message}")
                )
            }
        }
    }

    private fun clearForm() {
        updateState {
            copy(
                firstName = "",
                lastName = "",
                email = "",
                firstNameError = null,
                lastNameError = null,
                emailError = null,
                isLoading = false
            )
        }
    }
}
