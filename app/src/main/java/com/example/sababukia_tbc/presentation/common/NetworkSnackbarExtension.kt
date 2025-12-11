package com.example.sababukia_tbc.presentation.common

import android.view.View
import com.google.android.material.snackbar.Snackbar

/**
 * Shows a network disconnected snackbar.
 * This is an indefinite snackbar that stays until dismissed or connection is restored.
 */
fun View.showNetworkDisconnectedSnackbar(): Snackbar {
    return Snackbar.make(
        this,
        "No internet connection",
        Snackbar.LENGTH_INDEFINITE
    ).apply {
        setAction("Dismiss") { dismiss() }
        show()
    }
}

/**
 * Shows a network connected snackbar.
 * This is a short-duration snackbar that auto-dismisses.
 */
fun View.showNetworkConnectedSnackbar(): Snackbar {
    return Snackbar.make(
        this,
        "Back online",
        Snackbar.LENGTH_SHORT
    ).apply {
        show()
    }
}
