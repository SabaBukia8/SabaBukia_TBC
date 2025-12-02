package com.example.sababukia_tbc.domain.usecase

import com.example.sababukia_tbc.domain.validator.EmailValidator
import com.example.sababukia_tbc.domain.validator.ValidationResult
import javax.inject.Inject

class ValidateEmailUseCase @Inject constructor() {
    operator fun invoke(email: String): ValidationResult {
        return EmailValidator.validate(email)
    }
}
