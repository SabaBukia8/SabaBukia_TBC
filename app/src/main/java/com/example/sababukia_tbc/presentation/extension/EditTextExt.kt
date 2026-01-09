package com.example.sababukia_tbc.presentation.extension

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged

@OptIn(FlowPreview::class)
fun EditText.textChanges(debounceMs: Long = 300L): Flow<String> = callbackFlow {
    val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
        override fun afterTextChanged(s: Editable?) {
            trySend(s?.toString().orEmpty())
        }
    }
    addTextChangedListener(textWatcher)
    awaitClose { removeTextChangedListener(textWatcher) }
}
    .debounce(debounceMs)
    .distinctUntilChanged()

fun EditText.onTextChanged(action: (String) -> Unit) {
    addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
        override fun afterTextChanged(s: Editable?) {
            action(s?.toString().orEmpty())
        }
    })
}

fun TextInputLayout.setErrorMessage(message: String?) {
    error = message
    isErrorEnabled = message != null
}

fun TextInputLayout.clearError() {
    error = null
    isErrorEnabled = false
}
