package com.example.sababukia_tbc.presentation.register

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.sababukia_tbc.R
import com.example.sababukia_tbc.presentation.common.components.AppTextField
import com.example.sababukia_tbc.presentation.common.components.FormScreen
import com.example.sababukia_tbc.presentation.common.toMessage
import com.example.sababukia_tbc.ui.theme.SabaBukiaTBCTheme

@Composable
fun RegisterScreen(
    onNavigateBack: () -> Unit,
    onRegisterSuccess: () -> Unit,
    onShowSnackbar: (String) -> Unit,
    viewModel: RegisterViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.sideEffect.collect { effect ->
            when (effect) {
                RegisterSideEffect.NavigateToNickname -> onRegisterSuccess()
                is RegisterSideEffect.ShowMessage -> {
                    val message = when (effect.type) {
                        MessageType.SUCCESS -> context.getString(R.string.authentication_successful)
                        MessageType.ERROR -> effect.customMessage ?: context.getString(R.string.authentication_failed)
                    }
                    onShowSnackbar(message)
                }
            }
        }
    }

    RegisterScreenContent(
        state = state,
        snackbarHostState = snackbarHostState,
        onNavigateBack = onNavigateBack,
        onEmailChanged = { viewModel.onEvent(RegisterEvent.EmailChanged(it)) },
        onPasswordChanged = { viewModel.onEvent(RegisterEvent.PasswordChanged(it)) },
        onSubmit = { viewModel.onEvent(RegisterEvent.Submit) }
    )
}

@Composable
private fun RegisterScreenContent(
    state: RegisterState,
    snackbarHostState: SnackbarHostState,
    onNavigateBack: () -> Unit,
    onEmailChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onSubmit: () -> Unit
) {
    FormScreen(
        title = stringResource(R.string.register),
        snackbarHostState = snackbarHostState,
        buttonText = stringResource(R.string.next_button),
        onButtonClick = onSubmit,
        isLoading = state.isLoading,
        onNavigateBack = onNavigateBack
    ) {
        AppTextField(
            value = state.email,
            onValueChange = onEmailChanged,
            placeholder = stringResource(R.string.email_hint),
            isError = state.emailError != null,
            errorMessage = state.emailError?.toMessage(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        AppTextField(
            value = state.password,
            onValueChange = onPasswordChanged,
            placeholder = stringResource(R.string.password_hint),
            isPassword = true,
            isError = state.passwordError != null,
            errorMessage = state.passwordError?.toMessage(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun RegisterScreenPreview() {
    SabaBukiaTBCTheme {
        RegisterScreenContent(
            state = RegisterState(),
            snackbarHostState = SnackbarHostState(),
            onNavigateBack = {},
            onEmailChanged = {},
            onPasswordChanged = {},
            onSubmit = {}
        )
    }
}