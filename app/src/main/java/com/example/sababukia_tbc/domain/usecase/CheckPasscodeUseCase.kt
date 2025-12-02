package com.example.sababukia_tbc.domain.usecase

import javax.inject.Inject

class CheckPasscodeUseCase @Inject constructor() {
    companion object {
        private const val CORRECT_PASSCODE = "0934"
    }

    operator fun invoke(passcode: String): Boolean {
        return passcode == CORRECT_PASSCODE
    }
}
