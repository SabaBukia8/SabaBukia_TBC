package com.example.sababukia_tbc.presentation.screen.passcode

import com.example.sababukia_tbc.R
import com.example.sababukia_tbc.domain.repository.BiometricAuthRepository
import com.example.sababukia_tbc.domain.usecase.CheckPasscodeUseCase
import com.example.sababukia_tbc.domain.usecase.ValidatePasscodeUseCase
import com.example.sababukia_tbc.domain.validator.ValidationResult
import com.example.sababukia_tbc.presentation.common.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PasscodeViewModel @Inject constructor(
    private val validatePasscodeUseCase: ValidatePasscodeUseCase,
    private val checkPasscodeUseCase: CheckPasscodeUseCase,
    private val biometricAuthRepository: BiometricAuthRepository
) : BaseViewModel<PasscodeState, PasscodeEvent, PasscodeSideEffect>(
    initialState = PasscodeState()
) {

    companion object {
        private const val MAX_PASSCODE_LENGTH = 4
    }

    override fun onEvent(event: PasscodeEvent) {
        when (event) {
            is PasscodeEvent.OnDigitClick -> onDigitClick(event.digit)
            is PasscodeEvent.OnDeleteClick -> onDeleteClick()
            is PasscodeEvent.OnBiometricClick -> onBiometricClick()
            is PasscodeEvent.OnForgotPasswordClick -> onForgotPasswordClick()
            is PasscodeEvent.OnTryAgainClick -> onTryAgainClick()
            is PasscodeEvent.OnBiometricSuccess -> onBiometricSuccess()
            is PasscodeEvent.OnBiometricError -> onBiometricError(event.message)
            is PasscodeEvent.OnBiometricFailed -> onBiometricFailed()
        }
    }

    private fun onDigitClick(digit: String) {
        val currentPasscode = state.value.passcodeDigits
        if (currentPasscode.size < MAX_PASSCODE_LENGTH) {
            val newPasscode = currentPasscode + digit
            updateState { it.copy(passcodeDigits = newPasscode) }

            if (newPasscode.size == MAX_PASSCODE_LENGTH) {
                validateAndCheckPasscode(newPasscode.joinToString(""))
            }
        }
    }

    private fun onDeleteClick() {
        val currentPasscode = state.value.passcodeDigits
        if (currentPasscode.isNotEmpty()) {
            val newPasscode = currentPasscode.dropLast(1)
            updateState { it.copy(passcodeDigits = newPasscode) }
        }
    }

    private fun onForgotPasswordClick() {
        emitSideEffect(
            PasscodeSideEffect.ShowSnackbar(R.string.passcode_reminder)
        )
    }

    private fun onTryAgainClick() {
        updateState { PasscodeState() }
    }

    private fun onBiometricClick() {
        emitSideEffect(PasscodeSideEffect.TriggerBiometricAuth)
    }

    private fun onBiometricSuccess() {
        updateState { it.copy(showSuccess = true) }
        emitSideEffect(PasscodeSideEffect.ShowSuccess)
    }

    private fun onBiometricError(message: String) {
        emitSideEffect(PasscodeSideEffect.ShowError(message))
    }

    private fun onBiometricFailed() {
        emitSideEffect(PasscodeSideEffect.ShowErrorRes(R.string.biometric_error))
    }

    private fun validateAndCheckPasscode(passcode: String) {
        val validationResult = validatePasscodeUseCase(passcode)

        when (validationResult) {
            is ValidationResult.Invalid -> {
                emitSideEffect(PasscodeSideEffect.ShowError(validationResult.errorMessage))
                clearPasscode()
            }
            is ValidationResult.Valid -> {
                val isCorrect = checkPasscodeUseCase(passcode)
                if (isCorrect) {
                    updateState { it.copy(showSuccess = true) }
                    emitSideEffect(PasscodeSideEffect.ShowSuccess)
                } else {
                    emitSideEffect(PasscodeSideEffect.ShowErrorRes(R.string.error_invalid_passcode))
                    clearPasscode()
                }
            }
        }
    }

    private fun clearPasscode() {
        updateState { it.copy(passcodeDigits = emptyList()) }
        emitSideEffect(PasscodeSideEffect.ClearPasscode)
    }

    fun isBiometricAvailable(): Boolean {
        return biometricAuthRepository.isBiometricAvailable()
    }
}
