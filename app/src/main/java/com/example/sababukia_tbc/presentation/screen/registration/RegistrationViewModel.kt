package com.example.sababukia_tbc.presentation.screen.registration

import androidx.lifecycle.viewModelScope
import com.example.sababukia_tbc.domain.model.RegistrationError
import com.example.sababukia_tbc.domain.model.Result
import com.example.sababukia_tbc.domain.usecase.GetFormConfigurationUseCase
import com.example.sababukia_tbc.domain.usecase.ValidateFormUseCase
import com.example.sababukia_tbc.presentation.common.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegistrationViewModel @Inject constructor(
    private val getFormConfigurationUseCase: GetFormConfigurationUseCase,
    private val validateFormUseCase: ValidateFormUseCase
) : BaseViewModel<RegistrationState, RegistrationEvent, RegistrationSideEffect>(
    RegistrationState()
) {

    init {
        onEvent(RegistrationEvent.LoadForm)
    }

    override fun onEvent(event: RegistrationEvent) {
        when (event) {
            is RegistrationEvent.LoadForm -> loadForm()
            is RegistrationEvent.FieldValueChanged -> handleFieldValueChanged(event.fieldId, event.value)
            is RegistrationEvent.ChooserFieldClicked -> handleChooserFieldClicked(event.fieldId, event.hint)
            is RegistrationEvent.DismissDatePicker -> updateState { copy(showDatePicker = false, activeChooserFieldId = null) }
            is RegistrationEvent.DismissGenderPicker -> updateState { copy(showGenderPicker = false, activeChooserFieldId = null) }
            is RegistrationEvent.DateSelected -> handleDateSelected(event.date)
            is RegistrationEvent.GenderSelected -> handleGenderSelected(event.gender)
            is RegistrationEvent.Submit -> handleSubmit()
        }
    }

    private fun loadForm() {
        viewModelScope.launch {
            updateState { copy(isLoading = true, error = null) }

            when (val result = getFormConfigurationUseCase()) {
                is Result.Success -> {
                    updateState {
                        copy(
                            isLoading = false,
                            sections = result.data
                        )
                    }
                }
                is Result.Error -> {
                    val errorMessage = mapErrorToMessage(result.error)
                    updateState { copy(isLoading = false, error = errorMessage) }
                    emitSideEffect(RegistrationSideEffect.ShowError(errorMessage))
                }
            }
        }
    }

    private fun handleFieldValueChanged(fieldId: Int, value: String) {
        updateState {
            val newFieldValues = fieldValues.toMutableMap().apply {
                this[fieldId] = value
            }
            val newFieldErrors = fieldErrors.toMutableMap().apply {
                remove(fieldId)
            }
            copy(fieldValues = newFieldValues, fieldErrors = newFieldErrors)
        }
    }

    private fun handleChooserFieldClicked(fieldId: Int, hint: String) {
        val hintLower = hint.lowercase()
        when {
            hintLower.contains("birth") || hintLower.contains("date") -> {
                updateState { copy(showDatePicker = true, activeChooserFieldId = fieldId) }
            }
            hintLower.contains("gender") -> {
                updateState { copy(showGenderPicker = true, activeChooserFieldId = fieldId) }
            }
        }
    }

    private fun handleDateSelected(date: String) {
        val fieldId = state.value.activeChooserFieldId ?: return
        updateState {
            val newFieldValues = fieldValues.toMutableMap().apply {
                this[fieldId] = date
            }
            val newFieldErrors = fieldErrors.toMutableMap().apply {
                remove(fieldId)
            }
            copy(
                fieldValues = newFieldValues,
                fieldErrors = newFieldErrors,
                showDatePicker = false,
                activeChooserFieldId = null
            )
        }
    }

    private fun handleGenderSelected(gender: String) {
        val fieldId = state.value.activeChooserFieldId ?: return
        updateState {
            val newFieldValues = fieldValues.toMutableMap().apply {
                this[fieldId] = gender
            }
            val newFieldErrors = fieldErrors.toMutableMap().apply {
                remove(fieldId)
            }
            copy(
                fieldValues = newFieldValues,
                fieldErrors = newFieldErrors,
                showGenderPicker = false,
                activeChooserFieldId = null
            )
        }
    }

    private fun handleSubmit() {
        val currentState = state.value
        val errors = validateFormUseCase.getFieldErrors(currentState.sections, currentState.fieldValues)

        if (errors.isNotEmpty()) {
            updateState { copy(fieldErrors = errors) }

            val validationResult = validateFormUseCase(currentState.sections, currentState.fieldValues)
            if (validationResult is Result.Error && validationResult.error is RegistrationError.PinMismatch) {
                emitSideEffect(RegistrationSideEffect.ShowError("PINs do not match"))
            } else {
                emitSideEffect(RegistrationSideEffect.ShowError("Please fill in all required fields"))
            }
            return
        }

        emitSideEffect(RegistrationSideEffect.RegistrationSuccess)
    }

    private fun mapErrorToMessage(error: RegistrationError): String {
        return when (error) {
            is RegistrationError.Network -> "Network error. Please check your connection."
            is RegistrationError.Unknown -> error.message
            is RegistrationError.ValidationFailed -> "Please fill in all required fields."
            is RegistrationError.PinMismatch -> "PINs do not match."
        }
    }
}
