package com.example.sababukia_tbc.presentation.screen.registration

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.sababukia_tbc.R
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.sababukia_tbc.domain.model.FieldType
import com.example.sababukia_tbc.domain.model.FormField
import com.example.sababukia_tbc.domain.model.FormSection
import com.example.sababukia_tbc.domain.model.KeyboardType
import com.example.sababukia_tbc.presentation.common.LocalSnackbarController
import com.example.sababukia_tbc.presentation.common.extensions.CollectWithLifecycle
import com.example.sababukia_tbc.presentation.components.ErrorView
import com.example.sababukia_tbc.presentation.components.LoadingIndicator
import com.example.sababukia_tbc.presentation.components.registration.ChooserField
import com.example.sababukia_tbc.presentation.components.registration.GenderPickerDialog
import com.example.sababukia_tbc.presentation.components.registration.InputField
import com.example.sababukia_tbc.presentation.components.registration.RegistrationButton
import com.example.sababukia_tbc.presentation.components.registration.RegistrationDatePickerDialog
import com.example.sababukia_tbc.presentation.theme.AppTheme

@Composable
fun RegistrationScreen(
    viewModel: RegistrationViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarController = LocalSnackbarController.current
    val context = LocalContext.current
    val registrationSuccessMessage = context.getString(R.string.registration_success)

    viewModel.sideEffect.CollectWithLifecycle { sideEffect ->
        when (sideEffect) {
            is RegistrationSideEffect.ShowError -> {
                snackbarController.showSnackbar(sideEffect.message)
            }
            is RegistrationSideEffect.RegistrationSuccess -> {
                snackbarController.showSnackbar(registrationSuccessMessage)
            }
        }
    }

    RegistrationScreenContent(
        state = state,
        onEvent = viewModel::onEvent
    )
}

@Composable
private fun RegistrationScreenContent(
    state: RegistrationState,
    onEvent: (RegistrationEvent) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.colors.backgroundGradient)
    ) {
        when {
            state.isLoading && state.sections.isEmpty() -> {
                LoadingIndicator()
            }
            state.error != null && state.sections.isEmpty() -> {
                ErrorView(
                    message = state.error,
                    onRetry = { onEvent(RegistrationEvent.LoadForm) }
                )
            }
            else -> {
                RegistrationFormContent(
                    state = state,
                    onEvent = onEvent,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        if (state.showDatePicker) {
            RegistrationDatePickerDialog(
                onDismiss = { onEvent(RegistrationEvent.DismissDatePicker) },
                onDateSelected = { onEvent(RegistrationEvent.DateSelected(it)) }
            )
        }

        if (state.showGenderPicker) {
            GenderPickerDialog(
                onDismiss = { onEvent(RegistrationEvent.DismissGenderPicker) },
                onGenderSelected = { onEvent(RegistrationEvent.GenderSelected(it)) }
            )
        }
    }
}

@Composable
private fun RegistrationFormContent(
    state: RegistrationState,
    onEvent: (RegistrationEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(AppTheme.spacing.spacing16),
        verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.spacing16)
    ) {
        item {
            Text(
                text = stringResource(R.string.registration_title),
                style = AppTheme.typography.titleLarge,
                color = AppTheme.colors.textPrimary,
                modifier = Modifier.padding(bottom = AppTheme.spacing.spacing8)
            )
        }

        items(
            items = state.sections,
            key = { it.id }
        ) { section ->
            SectionContent(
                section = section,
                fieldValues = state.fieldValues,
                fieldErrors = state.fieldErrors,
                onEvent = onEvent
            )
        }

        item {
            Spacer(modifier = Modifier.height(AppTheme.spacing.spacing8))
            RegistrationButton(
                text = stringResource(R.string.registration_button),
                onClick = { onEvent(RegistrationEvent.Submit) },
                isLoading = state.isLoading,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun SectionContent(
    section: FormSection,
    fieldValues: Map<Int, String>,
    fieldErrors: Map<Int, String>,
    onEvent: (RegistrationEvent) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(AppTheme.radius.radius12),
        color = AppTheme.colors.cardBackground
    ) {
        Column(
            modifier = Modifier.padding(AppTheme.spacing.spacing16),
            verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.spacing12)
        ) {
            section.fields.forEach { field ->
                FormFieldItem(
                    field = field,
                    value = fieldValues[field.id].orEmpty(),
                    error = fieldErrors[field.id],
                    onValueChanged = { onEvent(RegistrationEvent.FieldValueChanged(field.id, it)) },
                    onChooserClicked = { onEvent(RegistrationEvent.ChooserFieldClicked(field.id, field.hint)) }
                )
            }
        }
    }
}

@Composable
private fun FormFieldItem(
    field: FormField,
    value: String,
    error: String?,
    onValueChanged: (String) -> Unit,
    onChooserClicked: () -> Unit
) {
    when (field.fieldType) {
        FieldType.INPUT -> {
            InputField(
                value = value,
                onValueChange = onValueChanged,
                hint = field.hint,
                keyboardType = field.keyboardType,
                isRequired = field.isRequired,
                error = error,
                iconUrl = field.iconUrl,
                modifier = Modifier.fillMaxWidth()
            )
        }
        FieldType.CHOOSER -> {
            ChooserField(
                value = value,
                onClick = onChooserClicked,
                hint = field.hint,
                isRequired = field.isRequired,
                error = error,
                iconUrl = field.iconUrl,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun RegistrationScreenContentPreview() {
    AppTheme {
        RegistrationScreenContent(
            state = RegistrationState(
                sections = listOf(
                    FormSection(
                        id = 0,
                        fields = listOf(
                            FormField(
                                id = 1,
                                hint = "Username",
                                fieldType = FieldType.INPUT,
                                keyboardType = KeyboardType.TEXT,
                                isRequired = true,
                                isActive = true,
                                iconUrl = null
                            ),
                            FormField(
                                id = 2,
                                hint = "Email",
                                fieldType = FieldType.INPUT,
                                keyboardType = KeyboardType.EMAIL,
                                isRequired = true,
                                isActive = true,
                                iconUrl = null
                            )
                        )
                    )
                )
            ),
            onEvent = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun RegistrationScreenLoadingPreview() {
    AppTheme {
        RegistrationScreenContent(
            state = RegistrationState(isLoading = true),
            onEvent = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun RegistrationScreenErrorPreview() {
    AppTheme {
        RegistrationScreenContent(
            state = RegistrationState(error = "Network error. Please check your connection."),
            onEvent = {}
        )
    }
}
