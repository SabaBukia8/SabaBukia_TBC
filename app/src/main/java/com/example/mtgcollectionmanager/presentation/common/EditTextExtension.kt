package com.example.mtgcollectionmanager.presentation.common

import android.widget.EditText
import androidx.core.widget.doAfterTextChanged

fun EditText.onTextChanged(callback: (String) -> Unit) {
    doAfterTextChanged {
        callback(it?.toString() ?: "")
    }
}
