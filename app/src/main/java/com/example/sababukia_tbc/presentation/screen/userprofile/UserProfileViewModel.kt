package com.example.sababukia_tbc.presentation.screen.userprofile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sababukia_tbc.UserProfile
import com.example.sababukia_tbc.UserProfiles
import com.example.sababukia_tbc.data.model.local.datastore.ProtoDataStoreManager
import com.example.sababukia_tbc.di.UserProfileDataStore
import com.example.sababukia_tbc.presentation.common.ValidationUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserProfileViewModel @Inject constructor(
    @UserProfileDataStore private val userProfileStore: ProtoDataStoreManager<UserProfiles>
) : ViewModel() {

    private val _state = MutableStateFlow(UserProfileState())
    val state = _state.asStateFlow()

    private val _sideEffect = MutableSharedFlow<UserProfileSideEffect>()
    val sideEffect = _sideEffect.asSharedFlow()

    fun onEvent(event: UserProfileEvent) {
        when (event) {
            is UserProfileEvent.OnFirstNameChanged -> updateFirstName(event.firstName)
            is UserProfileEvent.OnLastNameChanged -> updateLastName(event.lastName)
            is UserProfileEvent.OnEmailChanged -> updateEmail(event.email)
            is UserProfileEvent.OnSaveClicked -> saveUserProfile()
            is UserProfileEvent.OnReadClicked -> loadProfiles()
            is UserProfileEvent.OnDeleteProfile -> deleteProfile(event.profileId)
            is UserProfileEvent.OnClearForm -> clearForm()
        }
    }

    private fun updateFirstName(firstName: String) {
        _state.value = _state.value.copy(
            firstName = firstName,
            firstNameError = null
        )
    }

    private fun updateLastName(lastName: String) {
        _state.value = _state.value.copy(
            lastName = lastName,
            lastNameError = null
        )
    }

    private fun updateEmail(email: String) {
        _state.value = _state.value.copy(
            email = email,
            emailError = null
        )
    }

    private fun validateInputs(): Boolean {
        val currentState = _state.value
        var isValid = true

        val firstNameError = ValidationUtils.getNameErrorMessage("First name", currentState.firstName)
        val lastNameError = ValidationUtils.getNameErrorMessage("Last name", currentState.lastName)
        val emailError = ValidationUtils.getEmailErrorMessage(currentState.email)

        if (firstNameError != null || lastNameError != null || emailError != null) {
            _state.value = _state.value.copy(
                firstNameError = firstNameError,
                lastNameError = lastNameError,
                emailError = emailError
            )
            isValid = false
        }

        return isValid
    }

    private suspend fun checkEmailDuplicate(email: String, excludeId: Long? = null): Boolean {
        val currentProfiles = userProfileStore.data.first()
        return currentProfiles.profilesList.any {
            it.email.equals(email, ignoreCase = true) && it.id != excludeId
        }
    }

    private fun saveUserProfile() {
        viewModelScope.launch {
            try {
                if (!validateInputs()) {
                    return@launch
                }

                val currentState = _state.value

                if (checkEmailDuplicate(currentState.email)) {
                    _state.value = _state.value.copy(
                        emailError = "This email is already used"
                    )
                    _sideEffect.emit(
                        UserProfileSideEffect.ShowError("This email is already registered")
                    )
                    return@launch
                }

                _state.value = _state.value.copy(isLoading = true)

                val currentProfiles = userProfileStore.data.first()
                val nextId = currentProfiles.nextId

                val newProfile = UserProfile.newBuilder()
                    .setId(nextId)
                    .setFirstName(currentState.firstName.trim())
                    .setLastName(currentState.lastName.trim())
                    .setEmail(currentState.email.trim())
                    .build()

                userProfileStore.write { profiles ->
                    profiles.toBuilder()
                        .addProfiles(newProfile)
                        .setNextId(nextId + 1)
                        .build()
                }

                clearForm()
                _state.value = _state.value.copy(isLoading = false)

                _sideEffect.emit(
                    UserProfileSideEffect.ShowMessage("Profile saved successfully! Click 'Read' to view all profiles.")
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(isLoading = false)
                _sideEffect.emit(
                    UserProfileSideEffect.ShowError("Failed to save profile: ${e.message}")
                )
            }
        }
    }

    private fun loadProfiles() {
        viewModelScope.launch {
            try {
                _state.value = _state.value.copy(isLoading = true)

                val profiles = userProfileStore.data.first()

                val profileModels = profiles.profilesList.map { profile ->
                    UserProfileModel(
                        id = profile.id,
                        firstName = profile.firstName,
                        lastName = profile.lastName,
                        email = profile.email
                    )
                }

                _state.value = _state.value.copy(
                    savedProfiles = profileModels,
                    isLoading = false
                )

                if (profileModels.isNotEmpty()) {
                    _sideEffect.emit(
                        UserProfileSideEffect.ShowMessage("${profileModels.size} profile(s) loaded")
                    )
                } else {
                    _sideEffect.emit(
                        UserProfileSideEffect.ShowMessage("No saved profiles found")
                    )
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(isLoading = false)
                _sideEffect.emit(
                    UserProfileSideEffect.ShowError("Failed to load profiles: ${e.message}")
                )
            }
        }
    }

    private fun deleteProfile(profileId: Long) {
        viewModelScope.launch {
            try {
                _state.value = _state.value.copy(isLoading = true)

                userProfileStore.write { profiles ->
                    val updatedList = profiles.profilesList.filter { it.id != profileId }
                    profiles.toBuilder()
                        .clearProfiles()
                        .addAllProfiles(updatedList)
                        .build()
                }

                val updatedProfiles = _state.value.savedProfiles.filter { it.id != profileId }
                _state.value = _state.value.copy(
                    savedProfiles = updatedProfiles,
                    isLoading = false
                )

                _sideEffect.emit(
                    UserProfileSideEffect.ShowMessage("Profile deleted successfully")
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(isLoading = false)
                _sideEffect.emit(
                    UserProfileSideEffect.ShowError("Failed to delete profile: ${e.message}")
                )
            }
        }
    }

    private fun clearForm() {
        _state.value = _state.value.copy(
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
