package com.example.sababukia_tbc.presentation.screen.registration

import com.example.sababukia_tbc.domain.model.FormSection

data class RegistrationState(
    val isLoading: Boolean = false,
    val sections: List<FormSection> = emptyList(),
    val fieldValues: Map<Int, String> = emptyMap(),
    val fieldErrors: Map<Int, String> = emptyMap(),
    val error: String? = null,
    val showDatePicker: Boolean = false,
    val showGenderPicker: Boolean = false,
    val activeChooserFieldId: Int? = null
)

sealed interface RegistrationEvent {
    data object LoadForm : RegistrationEvent
    data class FieldValueChanged(val fieldId: Int, val value: String) : RegistrationEvent
    data class ChooserFieldClicked(val fieldId: Int, val hint: String) : RegistrationEvent
    data object DismissDatePicker : RegistrationEvent
    data object DismissGenderPicker : RegistrationEvent
    data class DateSelected(val date: String) : RegistrationEvent
    data class GenderSelected(val gender: String) : RegistrationEvent
    data object Submit : RegistrationEvent
}

sealed interface RegistrationSideEffect {
    data class ShowError(val message: String) : RegistrationSideEffect
    data object RegistrationSuccess : RegistrationSideEffect
}
