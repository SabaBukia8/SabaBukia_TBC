package com.example.sababukia_tbc.presentation.screen.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import com.example.sababukia_tbc.domain.usecase.GetAuthTokenUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.example.sababukia_tbc.presentation.ui.state.SplashUiState
import javax.inject.Inject


@HiltViewModel
class SplashViewModel @Inject constructor(
    private val getAuthTokenUseCase: GetAuthTokenUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SplashUiState())
    val uiState: StateFlow<SplashUiState> = _uiState.asStateFlow()

    init {
        checkAuthStatus()
    }


    private fun checkAuthStatus() {
        viewModelScope.launch {
            val token = getAuthTokenUseCase()
            _uiState.value = SplashUiState(
                isAuthenticated = !token.isNullOrEmpty(),
                isLoading = false
            )
        }
    }
}
