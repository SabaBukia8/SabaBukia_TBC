package com.example.sababukia_tbc.presentation.screen.home

sealed class HomeEvent {
    object OnProfileClicked : HomeEvent()
    object OnRetry : HomeEvent()
}
