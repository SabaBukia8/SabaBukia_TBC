package com.example.sababukia_tbc.domain.usecase

import com.example.sababukia_tbc.domain.validator.PasswordValidator
import com.example.sababukia_tbc.domain.validator.ValidationResult
import javax.inject.Inject

class ValidatePasswordUseCase @Inject constructor() {
    operator fun invoke(password: String): ValidationResult {
        return PasswordValidator.validate(password)
    }
}
