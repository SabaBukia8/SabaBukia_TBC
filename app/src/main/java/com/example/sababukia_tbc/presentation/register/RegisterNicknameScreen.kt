package com.example.sababukia_tbc.presentation.register

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
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
fun RegisterNicknameScreen(
    onComplete: () -> Unit,
    onShowSnackbar: (String) -> Unit,
    viewModel: NicknameViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.sideEffect.collect { effect ->
            when (effect) {
                NicknameSideEffect.NavigateToHome -> onComplete()
                is NicknameSideEffect.ShowMessage -> {
                    val message = when (effect.type) {
                        MessageType.SUCCESS -> context.getString(R.string.nickname_is_set)
                        MessageType.ERROR -> effect.customMessage ?: context.getString(R.string.couldn_t_set_a_nickname)
                    }
                    onShowSnackbar(message)
                }
            }
        }
    }

    RegisterNicknameScreenContent(
        state = state,
        snackbarHostState = snackbarHostState,
        onNicknameChanged = { viewModel.onEvent(NicknameEvent.NicknameChanged(it)) },
        onSubmit = { viewModel.onEvent(NicknameEvent.Submit) }
    )
}

@Composable
private fun RegisterNicknameScreenContent(
    state: NicknameState,
    snackbarHostState: SnackbarHostState,
    onNicknameChanged: (String) -> Unit,
    onSubmit: () -> Unit
) {
    FormScreen(
        title = stringResource(R.string.register),
        snackbarHostState = snackbarHostState,
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
                modifier = Modifier.padding(bottom = 16.dp)
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
    SabaBukiaTBCTheme {
        RegisterNicknameScreenContent(
            state = NicknameState(),
            snackbarHostState = SnackbarHostState(),
            onNicknameChanged = {},
            onSubmit = {}
        )
    }
}