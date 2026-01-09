package com.example.sababukia_tbc.presentation.extension

import android.content.Context
import com.example.sababukia_tbc.R
import com.example.sababukia_tbc.domain.common.ErrorType

fun ErrorType.toUserMessage(context: Context): String {
    return when (this) {
        is ErrorType.Network.NoInternet -> context.getString(R.string.error_network_no_internet)
        is ErrorType.Network.Timeout -> context.getString(R.string.error_network_timeout)
        is ErrorType.Network.Server -> context.getString(R.string.error_network_server, code)
        is ErrorType.Network.Unknown -> message

        is ErrorType.Auth.InvalidCredentials -> context.getString(R.string.error_auth_invalid_credentials)
        is ErrorType.Auth.Unauthorized -> context.getString(R.string.error_auth_unauthorized)
        is ErrorType.Auth.SessionExpired -> context.getString(R.string.error_auth_session_expired)

        is ErrorType.Validation.InvalidEmail -> context.getString(R.string.error_validation_invalid_email)
        is ErrorType.Validation.WeakPassword -> context.getString(R.string.error_validation_weak_password)
        is ErrorType.Validation.PasswordMismatch -> context.getString(R.string.error_validation_password_mismatch)
        is ErrorType.Validation.EmptyField -> context.getString(R.string.error_validation_empty_field)

        is ErrorType.Generic -> message
    }
}
