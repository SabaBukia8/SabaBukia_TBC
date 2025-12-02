package com.example.sababukia_tbc.domain.usecase

import com.example.sababukia_tbc.domain.validator.PasscodeValidator
import com.example.sababukia_tbc.domain.validator.ValidationResult
import javax.inject.Inject

class ValidatePasscodeUseCase @Inject constructor() {
    operator fun invoke(passcode: String): ValidationResult {
        return PasscodeValidator.validate(passcode)
    }
}
