package com.example.sababukia_tbc.presentation.screen.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sababukia_tbc.data.common.Resource
import com.example.sababukia_tbc.domain.repository.IAuthRepository
import com.example.sababukia_tbc.domain.usecase.LogoutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val logoutUseCase: LogoutUseCase,
    private val repository: IAuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileState())
    val state = _state.asStateFlow()

    private val _sideEffect = MutableSharedFlow<ProfileSideEffect>()
    val sideEffect = _sideEffect.asSharedFlow()

    private var logoutJob: Job? = null

    init {
        loadUserEmail()
    }

    fun onEvent(event: ProfileEvent) {
        when (event) {
            is ProfileEvent.OnLogout -> logout()
            is ProfileEvent.OnBackPressed -> onBackPressed()
        }
    }

    private fun loadUserEmail() {
        viewModelScope.launch {
            val email = repository.getEmail() ?: "No email"
            _state.value = _state.value.copy(userEmail = email)
        }
    }

    private fun logout() {
        logoutJob?.cancel()
        logoutJob = viewModelScope.launch {
            _state.value = _state.value.copy(loader = Resource.Loading(isLoading = true))

            logoutUseCase()

            _state.value = _state.value.copy(loader = Resource.Success(data = "Logged out"))
            _sideEffect.emit(ProfileSideEffect.NavigateToLogin)
        }
    }

    private fun onBackPressed() {
        viewModelScope.launch {
            _sideEffect.emit(ProfileSideEffect.NavigateBack)
        }
    }

    override fun onCleared() {
        super.onCleared()
        logoutJob?.cancel()
    }
}
