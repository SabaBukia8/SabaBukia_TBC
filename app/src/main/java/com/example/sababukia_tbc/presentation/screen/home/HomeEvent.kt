package com.example.sababukia_tbc.presentation.screen.home

sealed class HomeEvent {
    data object OnProfileClicked : HomeEvent()
    data object OnUserProfileClicked : HomeEvent()
    data object OnRetry : HomeEvent()
}
