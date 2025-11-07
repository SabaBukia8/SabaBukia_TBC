package util

import android.app.AlertDialog
import android.content.Context
import android.text.InputType
import android.widget.EditText
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

object PinInputUtil {

    fun showPinInput(
        context: Context,
        title: String,
        currentPin: String? = null,
        onPinEntered: (String) -> Unit
    ) {
        val textInputLayout = TextInputLayout(context).apply {
            hint = title
            endIconMode = TextInputLayout.END_ICON_PASSWORD_TOGGLE
        }

        val editText = TextInputEditText(context).apply {
            inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
            maxLines = 1
            setText(currentPin.orEmpty())
            selectAll()
        }

        textInputLayout.addView(editText)

        val dialog = AlertDialog.Builder(context)
            .setTitle(title)
            .setView(textInputLayout)
            .setPositiveButton("OK") { _, _ ->
                val pin = editText.text?.toString()?.trim() ?: ""
                onPinEntered(pin)
            }
            .setNegativeButton("Cancel", null)
            .create()

        dialog.show()

        // Request focus and show keyboard
        editText.requestFocus()
        editText.post {
            val imm =
                context.getSystemService(Context.INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
            imm.showSoftInput(editText, android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT)
        }
    }
}