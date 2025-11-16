package com.example.sababukia_tbc.presentation.util

import android.content.Context
import androidx.annotation.StringRes
import com.example.sababukia_tbc.R


object StringResourceResolver {

    private var applicationContext: Context? = null


    fun initialize(context: Context) {
        applicationContext = context.applicationContext
    }


    fun getString(@StringRes stringResId: Int, vararg formatArgs: Any): String {
        val context = applicationContext
        return context?.getString(stringResId, *formatArgs) ?: getResourceName(stringResId)
    }


    private fun getResourceName(@StringRes stringResId: Int): String {
        return when (stringResId) {
            R.string.error_navigation -> "error_navigation"
            R.string.error_login_failed -> "error_login_failed"
            R.string.error_registration_failed -> "error_registration_failed"
            R.string.error_network -> "error_network"
            R.string.error_incorrect_password -> "error_incorrect_password"
            R.string.error_empty_response -> "error_empty_response"
            R.string.http_error_format -> "http_error_format"
            R.string.api_error_format -> "api_error_format"
            R.string.error_loading_failed -> "error_loading_failed"
            else -> "unknown_string_resource_$stringResId"
        }
    }
}
