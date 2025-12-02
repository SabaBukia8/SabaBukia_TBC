package com.example.sababukia_tbc.presentation.screen.passcode

import com.example.sababukia_tbc.presentation.common.UiText

sealed class PasscodeSideEffect {
    object ShowSuccess : PasscodeSideEffect()
    data class ShowError(val message: UiText) : PasscodeSideEffect()
    data class ShowSnackbar(val message: UiText) : PasscodeSideEffect()
    object ClearPasscode : PasscodeSideEffect()
}
