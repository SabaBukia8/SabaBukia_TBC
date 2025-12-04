package com.example.sababukia_tbc.presentation.screen.passcode

import androidx.annotation.StringRes

sealed class PasscodeSideEffect {
    object ShowSuccess : PasscodeSideEffect()
    data class ShowError(val message: String) : PasscodeSideEffect()
    data class ShowErrorRes(@param:StringRes val messageResId: Int) : PasscodeSideEffect()
    data class ShowSnackbar(@param:StringRes val messageResId: Int) : PasscodeSideEffect()
    object ClearPasscode : PasscodeSideEffect()
    object TriggerBiometricAuth : PasscodeSideEffect()
}
