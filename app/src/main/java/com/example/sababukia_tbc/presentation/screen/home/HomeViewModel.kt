package com.example.sababukia_tbc.presentation.screen.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import com.example.sababukia_tbc.domain.usecase.GetUserInfoUseCase
import com.example.sababukia_tbc.domain.usecase.LogoutUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import com.example.sababukia_tbc.presentation.ui.state.HomeUiState
import com.example.sababukia_tbc.presentation.ui.navigation.NavigationEvent
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val logoutUseCase: LogoutUseCase,
    private val getUserInfoUseCase: GetUserInfoUseCase
) : ViewModel() {

    companion object {
        private const val TAG = "HomeViewModel"
    }

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<NavigationEvent>()
    val navigationEvent: SharedFlow<NavigationEvent> = _navigationEvent.asSharedFlow()

    init {
        loadUserInfo()
    }

    private fun loadUserInfo() {
        viewModelScope.launch {
            val userInfo = getUserInfoUseCase()
            _uiState.value = _uiState.value.copy(
                username = userInfo.username,
                email = userInfo.email,
                userId = userInfo.userId
            )
            Log.d(TAG, "User info loaded: ${userInfo.username}, ${userInfo.email}")
        }
    }

    fun logout() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)
                logoutUseCase()
                Log.d(TAG, "Logout successful")
                _uiState.value = _uiState.value.copy(isLoading = false)
                _navigationEvent.emit(NavigationEvent.NavigateToWelcome)
            } catch (e: Exception) {
                Log.e(TAG, "Logout failed", e)
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }
}
