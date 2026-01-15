package com.example.sababukia_tbc.presentation.login

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
import com.example.sababukia_tbc.domain.util.ValidationError
import com.example.sababukia_tbc.presentation.common.components.AppTextField
import com.example.sababukia_tbc.presentation.common.components.FormScreen
import com.example.sababukia_tbc.presentation.common.toMessage
import com.example.sababukia_tbc.ui.theme.SabaBukiaTBCTheme

@Composable
fun LoginScreen(
    onNavigateBack: () -> Unit,
    onLoginSuccess: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.sideEffect.collect { effect ->
            when (effect) {
                LoginSideEffect.NavigateToHome -> onLoginSuccess()
                is LoginSideEffect.ShowMessage -> {
                    val message = when (effect.type) {
                        MessageType.SUCCESS -> context.getString(R.string.authentication_successful)
                        MessageType.ERROR -> effect.customMessage ?: context.getString(R.string.authentication_failed)
                    }
                    snackbarHostState.showSnackbar(message)
                }
            }
        }
    }

    LoginScreenContent(
        state = state,
        snackbarHostState = snackbarHostState,
        onNavigateBack = onNavigateBack,
        onEmailChanged = { viewModel.onEvent(LoginEvent.EmailChanged(it)) },
        onPasswordChanged = { viewModel.onEvent(LoginEvent.PasswordChanged(it)) },
        onSubmit = { viewModel.onEvent(LoginEvent.Submit) }
    )
}

@Composable
private fun LoginScreenContent(
    state: LoginState,
    snackbarHostState: SnackbarHostState,
    onNavigateBack: () -> Unit,
    onEmailChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onSubmit: () -> Unit
) {
    FormScreen(
        title = stringResource(R.string.log_in),
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
private fun LoginScreenPreview() {
    SabaBukiaTBCTheme {
        LoginScreenContent(
            state = LoginState(),
            snackbarHostState = SnackbarHostState(),
            onNavigateBack = {},
            onEmailChanged = {},
            onPasswordChanged = {},
            onSubmit = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LoginScreenWithErrorPreview() {
    SabaBukiaTBCTheme {
        LoginScreenContent(
            state = LoginState(
                email = "invalid-email",
                emailError = ValidationError.INVALID_EMAIL_FORMAT,
                password = "123",
                passwordError = ValidationError.PASSWORD_TOO_SHORT
            ),
            snackbarHostState = SnackbarHostState(),
            onNavigateBack = {},
            onEmailChanged = {},
            onPasswordChanged = {},
            onSubmit = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LoginScreenLoadingPreview() {
    SabaBukiaTBCTheme {
        LoginScreenContent(
            state = LoginState(
                email = "user@example.com",
                password = "password123",
                isLoading = true
            ),
            snackbarHostState = SnackbarHostState(),
            onNavigateBack = {},
            onEmailChanged = {},
            onPasswordChanged = {},
            onSubmit = {}
        )
    }
}
