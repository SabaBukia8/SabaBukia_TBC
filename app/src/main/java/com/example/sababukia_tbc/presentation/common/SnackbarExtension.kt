package com.example.sababukia_tbc.presentation.common

import android.view.View
import com.google.android.material.snackbar.Snackbar

fun View.showSnackbar(
    message: String,
    duration: Int = Snackbar.LENGTH_SHORT,
    actionText: String? = null,
    action: (() -> Unit)? = null
) {
    val snackbar = Snackbar.make(this, message, duration)
    if (actionText != null && action != null) {
        snackbar.setAction(actionText) { action() }
    }
    snackbar.show()
}

fun View.showSuccessSnackbar(
    message: String,
    duration: Int = Snackbar.LENGTH_SHORT
) {
    showSnackbar(message, duration)
}

fun View.showErrorSnackbar(
    message: String,
    duration: Int = Snackbar.LENGTH_LONG
) {
    showSnackbar(message, duration)
}
