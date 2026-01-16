package com.example.sababukia_tbc.presentation.common

import android.content.Context
import com.example.sababukia_tbc.R
import com.example.sababukia_tbc.domain.model.AuthError

fun AuthError.toMessage(context: Context): String = when (this) {
    AuthError.UserNotFound -> context.getString(R.string.error_user_not_found)
    AuthError.InvalidCredentials -> context.getString(R.string.error_invalid_credentials)
    AuthError.EmailAlreadyInUse -> context.getString(R.string.error_email_already_used)
    AuthError.UserIsNull -> context.getString(R.string.error_user_is_null)
    AuthError.UserNotSignedIn -> context.getString(R.string.error_user_not_signed_in)
    AuthError.NetworkError -> context.getString(R.string.error_network)
    AuthError.NicknameUpdateFailed -> context.getString(R.string.error_nickname_update_failed)
    is AuthError.Unknown -> context.getString(R.string.error_unknown)
}
