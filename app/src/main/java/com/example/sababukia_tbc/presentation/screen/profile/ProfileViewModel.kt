package com.example.sababukia_tbc.presentation.screen.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sababukia_tbc.domain.repository.IAuthRepository
import com.example.sababukia_tbc.domain.usecase.LogoutUseCase
import com.example.sababukia_tbc.presentation.ui.navigation.NavigationEvent
import com.example.sababukia_tbc.presentation.ui.state.ProfileUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val logoutUseCase: LogoutUseCase,
    private val repository: IAuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<NavigationEvent>()
    val navigationEvent: SharedFlow<NavigationEvent> = _navigationEvent.asSharedFlow()

    init {
        loadUserEmail()
    }

    private fun loadUserEmail() {
        viewModelScope.launch {
            val email = repository.getEmail() ?: "No email"
            _uiState.value = _uiState.value.copy(email = email)
        }
    }

    fun onLogoutClicked() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            logoutUseCase()
            _uiState.value = _uiState.value.copy(isLoading = false)
            _navigationEvent.emit(NavigationEvent.NavigateToLogin)
        }
    }
}
