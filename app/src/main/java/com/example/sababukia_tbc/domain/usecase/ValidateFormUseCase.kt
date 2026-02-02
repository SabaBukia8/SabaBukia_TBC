package com.example.sababukia_tbc.domain.usecase

import com.example.sababukia_tbc.domain.model.FormField
import com.example.sababukia_tbc.domain.model.FormSection
import com.example.sababukia_tbc.domain.model.RegistrationError
import com.example.sababukia_tbc.domain.model.Result
import javax.inject.Inject

class ValidateFormUseCase @Inject constructor() {

    operator fun invoke(
        sections: List<FormSection>,
        fieldValues: Map<Int, String>
    ): Result<Unit, RegistrationError> {
        val allFields = sections.flatMap { it.fields }

        // Check required fields
        val emptyRequiredFields = allFields
            .filter { it.isRequired }
            .filter { field -> fieldValues[field.id].isNullOrBlank() }

        if (emptyRequiredFields.isNotEmpty()) {
            return Result.Error(RegistrationError.ValidationFailed)
        }

        // Check PIN match
        val pinField = allFields.find { isPinField(it) }
        val confirmPinField = allFields.find { isConfirmPinField(it) }

        if (pinField != null && confirmPinField != null) {
            val pin = fieldValues[pinField.id].orEmpty()
            val confirmPin = fieldValues[confirmPinField.id].orEmpty()

            if (pin.isNotBlank() && confirmPin.isNotBlank() && pin != confirmPin) {
                return Result.Error(RegistrationError.PinMismatch)
            }
        }

        return Result.Success(Unit)
    }

    fun getFieldErrors(
        sections: List<FormSection>,
        fieldValues: Map<Int, String>
    ): Map<Int, String> {
        val errors = mutableMapOf<Int, String>()
        val allFields = sections.flatMap { it.fields }

        // Check required fields
        allFields
            .filter { it.isRequired }
            .filter { field -> fieldValues[field.id].isNullOrBlank() }
            .forEach { field -> errors[field.id] = "Field is not filled (${field.hint})" }

        // Check PIN match
        val pinField = allFields.find { isPinField(it) }
        val confirmPinField = allFields.find { isConfirmPinField(it) }

        if (pinField != null && confirmPinField != null) {
            val pin = fieldValues[pinField.id].orEmpty()
            val confirmPin = fieldValues[confirmPinField.id].orEmpty()

            if (pin.isNotBlank() && confirmPin.isNotBlank() && pin != confirmPin) {
                errors[confirmPinField.id] = "PINs do not match"
            }
        }

        return errors
    }

    private fun isPinField(field: FormField): Boolean {
        val hint = field.hint.lowercase()
        return hint.contains("pin") && !hint.contains("confirm")
    }

    private fun isConfirmPinField(field: FormField): Boolean {
        val hint = field.hint.lowercase()
        return hint.contains("confirm") && hint.contains("pin")
    }
}
