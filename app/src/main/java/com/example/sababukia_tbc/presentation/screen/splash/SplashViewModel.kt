package com.example.sababukia_tbc.presentation.screen.splash

import android.os.CountDownTimer
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor() : ViewModel() {

    private val _navigationEvent = MutableStateFlow(false)
    val navigationEvent: StateFlow<Boolean> = _navigationEvent.asStateFlow()

    private var countDownTimer: CountDownTimer? = null

    init {
        startSplashTimer()
    }

    private fun startSplashTimer() {
        countDownTimer = object : CountDownTimer(1500, 1500) {
            override fun onTick(millisUntilFinished: Long) {
            }

            override fun onFinish() {
                _navigationEvent.value = true
            }
        }.start()
    }

    override fun onCleared() {
        super.onCleared()
        countDownTimer?.cancel()
    }
}
