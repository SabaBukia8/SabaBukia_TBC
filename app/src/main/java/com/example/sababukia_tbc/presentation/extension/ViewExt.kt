package com.example.sababukia_tbc.presentation.extension

import android.view.View
import androidx.core.view.isVisible
import com.google.android.material.snackbar.Snackbar

fun View.show() {
    isVisible = true
}

fun View.hide() {
    isVisible = false
}

fun View.enable() {
    isEnabled = true
}

fun View.disable() {
    isEnabled = false
}

fun View.onClick(debounceTime: Long = 300L, action: () -> Unit) {
    var lastClickTime = 0L
    setOnClickListener {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastClickTime >= debounceTime) {
            lastClickTime = currentTime
            action()
        }
    }
}

fun View.showSnackbar(
    message: String,
    duration: Int = Snackbar.LENGTH_SHORT
) {
    Snackbar.make(this, message, duration).show()
}
