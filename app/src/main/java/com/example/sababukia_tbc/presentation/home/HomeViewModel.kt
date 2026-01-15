package com.example.sababukia_tbc.presentation.home

import com.example.sababukia_tbc.domain.repository.SessionRepository
import com.example.sababukia_tbc.presentation.common.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val sessionRepository: SessionRepository
) : BaseViewModel<HomeState, HomeEvent, HomeSideEffect>(
    HomeState(isLoggedIn = sessionRepository.isLoggedIn)
) {

    override fun onEvent(event: HomeEvent) {
        when (event) {
            HomeEvent.Logout -> logout()
            HomeEvent.RefreshAuthState -> refreshAuthState()
        }
    }

    private fun logout() {
        sessionRepository.logout()
        updateState { copy(isLoggedIn = false) }
        sendSideEffect(HomeSideEffect.LoggedOut)
    }

    private fun refreshAuthState() {
        updateState { copy(isLoggedIn = sessionRepository.isLoggedIn) }
    }
}

data class HomeState(
    val isLoggedIn: Boolean = false
)

sealed class HomeEvent {
    data object Logout : HomeEvent()
    data object RefreshAuthState : HomeEvent()
}

sealed class HomeSideEffect {
    data object LoggedOut : HomeSideEffect()
}
