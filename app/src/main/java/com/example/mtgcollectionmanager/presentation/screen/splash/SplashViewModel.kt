package com.example.mtgcollectionmanager.presentation.screen.splash

import androidx.lifecycle.viewModelScope
import com.example.mtgcollectionmanager.domain.usecase.auth.IsUserLoggedInUseCase
import com.example.mtgcollectionmanager.presentation.common.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val isUserLoggedInUseCase: IsUserLoggedInUseCase
) : BaseViewModel<SplashContract.State, SplashContract.Event, SplashContract.SideEffect>(
    SplashContract.State()
) {

    init {
        onEvent(SplashContract.Event.CheckAuthStatus)
    }

    override fun onEvent(event: SplashContract.Event) {
        when (event) {
            is SplashContract.Event.CheckAuthStatus -> checkAuthStatus()
        }
    }

    private fun checkAuthStatus() {
        viewModelScope.launch {
            delay(1500)

            if (isUserLoggedInUseCase()) {
                emitSideEffect(SplashContract.SideEffect.NavigateToCollectionsList)
            } else {
                emitSideEffect(SplashContract.SideEffect.NavigateToLogin)
            }
        }
    }
}
