package com.example.mtgcollectionmanager.presentation.common

import android.view.View
import com.google.android.material.snackbar.Snackbar

fun View.showSnackbar(
    message: String,
    duration: Int = Snackbar.LENGTH_SHORT,
    actionText: String? = null,
    action: (() -> Unit)? = null
) {
    Snackbar.make(this, message, duration).apply {
        if (actionText != null && action != null) {
            setAction(actionText) { action() }
        }
        show()
    }
}

fun View.showSuccessSnackbar(message: String, duration: Int = Snackbar.LENGTH_SHORT) {
    showSnackbar(message, duration)
}

fun View.showErrorSnackbar(message: String, duration: Int = Snackbar.LENGTH_LONG) {
    showSnackbar(message, duration)
}
