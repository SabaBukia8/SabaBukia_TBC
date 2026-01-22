package com.example.sababukia_tbc.presentation.register

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
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
fun RegisterNicknameScreen(
    onComplete: () -> Unit,
    onShowSnackbar: (String) -> Unit,
    viewModel: NicknameViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    viewModel.sideEffect.CollectWithLifecycle { effect ->
        when (effect) {
            NicknameSideEffect.NavigateToHome -> onComplete()
            NicknameSideEffect.ShowSuccess -> {
                onShowSnackbar(context.getString(R.string.nickname_is_set))
            }
            is NicknameSideEffect.ShowError -> {
                onShowSnackbar(effect.error.toMessage(context))
            }
        }
    }

    RegisterNicknameScreenContent(
        state = state,
        onNicknameChanged = { viewModel.onEvent(NicknameEvent.NicknameChanged(it)) },
        onSubmit = { viewModel.onEvent(NicknameEvent.Submit) }
    )
}

@Composable
private fun RegisterNicknameScreenContent(
    state: NicknameState,
    onNicknameChanged: (String) -> Unit,
    onSubmit: () -> Unit
) {
    FormScreen(
        title = stringResource(R.string.register),
        buttonText = stringResource(R.string.next_button),
        onButtonClick = onSubmit,
        isLoading = state.isLoading,
        footerContent = {
            Text(
                text = buildAnnotatedString {
                    append("By signing up, you agree to Photo's ")
                    withStyle(style = SpanStyle(textDecoration = TextDecoration.Underline)) {
                        append("Terms of Service")
                    }
                    append(" and ")
                    withStyle(style = SpanStyle(textDecoration = TextDecoration.Underline)) {
                        append("Privacy Policy")
                    }
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                modifier = Modifier.padding(bottom = Spacing.spacer16)
            )
        }
    ) {
        AppTextField(
            value = state.nickname,
            onValueChange = onNicknameChanged,
            placeholder = stringResource(R.string.nickname),
            isError = state.nicknameError != null,
            errorMessage = state.nicknameError?.toMessage(),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun RegisterNicknameScreenPreview() {
    ApplicationTheme {
        RegisterNicknameScreenContent(
            state = NicknameState(),
            onNicknameChanged = {},
            onSubmit = {}
        )
    }
}
