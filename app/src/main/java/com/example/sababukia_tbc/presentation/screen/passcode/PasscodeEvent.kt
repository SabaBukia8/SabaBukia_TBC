package com.example.sababukia_tbc.presentation.screen.passcode

sealed class PasscodeEvent {
    data class OnDigitClick(val digit: String) : PasscodeEvent()
    object OnDeleteClick : PasscodeEvent()
    object OnBiometricClick : PasscodeEvent()
    object OnForgotPasswordClick : PasscodeEvent()
    object OnTryAgainClick : PasscodeEvent()
    object OnBiometricSuccess : PasscodeEvent()
    data class OnBiometricError(val message: String) : PasscodeEvent()
    object OnBiometricFailed : PasscodeEvent()
}
