package com.example.sababukia_tbc.presentation.login

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.sababukia_tbc.R
import com.example.sababukia_tbc.presentation.common.components.AppTextField
import com.example.sababukia_tbc.presentation.common.components.FormScreen
import com.example.sababukia_tbc.presentation.common.extensions.CollectWithLifecycle
import com.example.sababukia_tbc.presentation.common.toMessage
import com.example.sababukia_tbc.ui.theme.ApplicationTheme
import com.example.sababukia_tbc.ui.theme.Spacing

@Composable
fun LoginScreen(
    onNavigateBack: () -> Unit,
    onLoginSuccess: () -> Unit,
    onShowSnackbar: (String) -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    viewModel.sideEffect.CollectWithLifecycle { effect ->
        when (effect) {
            LoginSideEffect.NavigateToHome -> onLoginSuccess()
            LoginSideEffect.ShowSuccess -> {
                onShowSnackbar(context.getString(R.string.authentication_successful))
            }
            is LoginSideEffect.ShowError -> {
                onShowSnackbar(effect.error.toMessage(context))
            }
        }
    }

    LoginScreenContent(
        state = state,
        onNavigateBack = onNavigateBack,
        onEmailChanged = { viewModel.onEvent(LoginEvent.EmailChanged(it)) },
        onPasswordChanged = { viewModel.onEvent(LoginEvent.PasswordChanged(it)) },
        onSubmit = { viewModel.onEvent(LoginEvent.Submit) }
    )
}

@Composable
private fun LoginScreenContent(
    state: LoginState,
    onNavigateBack: () -> Unit,
    onEmailChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onSubmit: () -> Unit
) {
    FormScreen(
        title = stringResource(R.string.log_in),
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

        Spacer(modifier = Modifier.height(Spacing.spacer16))

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
    ApplicationTheme {
        LoginScreenContent(
            state = LoginState(),
            onNavigateBack = {},
            onEmailChanged = {},
            onPasswordChanged = {},
            onSubmit = {}
        )
    }
}
