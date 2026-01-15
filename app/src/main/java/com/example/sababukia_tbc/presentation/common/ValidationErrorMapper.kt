package com.example.sababukia_tbc.presentation.common

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.sababukia_tbc.R
import com.example.sababukia_tbc.domain.util.ValidationError

@Composable
fun ValidationError.toMessage(): String = when (this) {
    ValidationError.EMPTY_EMAIL -> stringResource(R.string.please_enter_an_email)
    ValidationError.INVALID_EMAIL_FORMAT -> stringResource(R.string.please_enter_a_correct_email)
    ValidationError.EMPTY_PASSWORD -> stringResource(R.string.please_enter_a_password)
    ValidationError.PASSWORD_TOO_SHORT -> stringResource(R.string.password_must_be_at_least_8_characters_long)
    ValidationError.EMPTY_NICKNAME -> stringResource(R.string.please_enter_a_nickname)
}
