package com.example.sababukia_tbc.presentation.screen.home

import com.example.sababukia_tbc.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
) : BaseViewModel<HomeState, HomeEvent, HomeSideEffect>(
    initialState = HomeState()
) {

    override fun onEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.OnProfileClicked -> onProfileClicked()
            is HomeEvent.OnUserProfileClicked -> onUserProfileClicked()
            is HomeEvent.OnRetry -> {}
        }
    }

    private fun onProfileClicked() {
        sendSideEffect(HomeSideEffect.NavigateToProfile)
    }

    private fun onUserProfileClicked() {
        sendSideEffect(HomeSideEffect.NavigateToUserProfile)
    }
}
