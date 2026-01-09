package com.example.sababukia_tbc.presentation.screen.profile

import androidx.lifecycle.viewModelScope
import com.example.sababukia_tbc.domain.common.Resource
import com.example.sababukia_tbc.domain.repository.UserPreferencesRepository
import com.example.sababukia_tbc.domain.usecase.LogoutUseCase
import com.example.sababukia_tbc.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val logoutUseCase: LogoutUseCase,
    private val userPreferencesRepository: UserPreferencesRepository
) : BaseViewModel<ProfileState, ProfileEvent, ProfileSideEffect>(
    initialState = ProfileState()
) {

    init {
        loadUserEmail()
    }

    override fun onEvent(event: ProfileEvent) {
        when (event) {
            is ProfileEvent.OnLogout -> logout()
            is ProfileEvent.OnBackPressed -> onBackPressed()
        }
    }

    private fun loadUserEmail() {
        viewModelScope.launch {
            val email = userPreferencesRepository.getEmail() ?: "No email"
            updateState { copy(userEmail = email) }
        }
    }

    private fun logout() {
        viewModelScope.launch {
            updateState { copy(loader = Resource.Loading(isLoading = true)) }

            logoutUseCase()

            updateState { copy(loader = Resource.Success(data = "Logged out")) }
            sendSideEffect(ProfileSideEffect.NavigateToLogin)
        }
    }

    private fun onBackPressed() {
        sendSideEffect(ProfileSideEffect.NavigateBack)
    }
}
