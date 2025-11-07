package screen.registration

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.sababukia_tbc.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import model.FieldValue
import model.FormSection
import repository.FormRepository

sealed class RegistrationUiState {
    object Loading : RegistrationUiState()
    data class Success(val sections: List<FormSection>) : RegistrationUiState()
    data class Error(val message: String) : RegistrationUiState()
}

sealed class ValidationResult {
    object Valid : ValidationResult()
    data class Invalid(val message: String) : ValidationResult()
}

class RegistrationViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = FormRepository(application)

    private val _uiState = MutableStateFlow<RegistrationUiState>(RegistrationUiState.Loading)
    val uiState: StateFlow<RegistrationUiState> = _uiState.asStateFlow()

    private val _fieldValues = MutableStateFlow<Map<Int, String>>(emptyMap())

    private val _chooserDialog = MutableStateFlow<ChooserDialog>(ChooserDialog.Hide)
    val chooserDialog: StateFlow<ChooserDialog> = _chooserDialog.asStateFlow()

    private var formSections: List<FormSection> = emptyList()

    init {
        loadFormConfiguration()
    }

    private fun loadFormConfiguration() {
        viewModelScope.launch {
            _uiState.value = RegistrationUiState.Loading
            try {
                formSections = repository.loadFormConfiguration()
                _uiState.value = RegistrationUiState.Success(formSections)
            } catch (e: Exception) {
                _uiState.value = RegistrationUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun onChooserClicked(fieldId: Int, title: String) {
        val options = when {
            title.contains("gender", ignoreCase = true) -> listOf("Male", "Female")
            else -> emptyList()
        }
        _chooserDialog.value = ChooserDialog.Show(fieldId, title, options)
    }

    fun onChooserOptionSelected(fieldId: Int, option: String) {
        updateFieldValue(fieldId, option)
        hideChooserDialog()
    }

    fun hideChooserDialog() {
        _chooserDialog.value = ChooserDialog.Hide
    }

    fun updateFieldValue(fieldId: Int, value: String) {
        _fieldValues.value = _fieldValues.value.toMutableMap().apply {
            put(fieldId, value)
        }
    }

    fun validateAndSubmit(): ValidationResult {
        val allFields = formSections.flatMap { it.fields }

        for (field in allFields) {
            if (field.required) {
                val value = _fieldValues.value[field.fieldId]
                if (value.isNullOrBlank()) {
                    val errorMessage = getApplication<Application>().getString(R.string.field_is_not_filled, field.hint)
                    return ValidationResult.Invalid(errorMessage)
                }
            }
        }

        val pinField = allFields.find { it.hint.equals("PIN", ignoreCase = true) }
        val confirmPinField = allFields.find { it.hint.equals("Confirm PIN", ignoreCase = true) }

        if (pinField != null && confirmPinField != null) {
            val pinValue = _fieldValues.value[pinField.fieldId] ?: ""
            val confirmPinValue = _fieldValues.value[confirmPinField.fieldId] ?: ""

            if (pinValue != confirmPinValue) {
                return ValidationResult.Invalid(getApplication<Application>().getString(R.string.pin_mismatch))
            }
        }

        submitRegistration()
        return ValidationResult.Valid
    }

    private fun submitRegistration() {
        val registrationData = mutableMapOf<Int, String>()

        _fieldValues.value.forEach { (fieldId, value) ->
            registrationData[fieldId] = value
        }

        val dataContainer = registrationData.map { (fieldId, value) ->
            FieldValue(fieldId, value)
        }

        println("=== REGISTRATION DATA ===")
        println("Data Container (field_id -> value):")
        dataContainer.forEach { fieldValue ->
            println("  ${fieldValue.fieldId} -> \"${fieldValue.value}\"")
        }
        println("========================")
    }

    fun getFieldValue(fieldId: Int): String {
        return _fieldValues.value[fieldId] ?: ""
    }
}