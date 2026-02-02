package com.example.sababukia_tbc.presentation.components.registration

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.sababukia_tbc.domain.model.KeyboardType
import com.example.sababukia_tbc.presentation.theme.AppTheme
import androidx.compose.ui.text.input.KeyboardType as ComposeKeyboardType

@Composable
fun InputField(
    value: String,
    onValueChange: (String) -> Unit,
    hint: String,
    keyboardType: KeyboardType,
    isRequired: Boolean,
    error: String?,
    iconUrl: String?,
    modifier: Modifier = Modifier
) {
    val isPassword = hint.lowercase().contains("pin") || hint.lowercase().contains("password")

    Column(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text(
                    text = if (isRequired) "$hint *" else hint,
                    style = AppTheme.typography.bodyMedium,
                    color = AppTheme.colors.textSecondary
                )
            },
            leadingIcon = iconUrl?.let {
                {
                    AsyncImage(
                        model = it,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                }
            },
            isError = error != null,
            visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
            keyboardOptions = KeyboardOptions(
                keyboardType = keyboardType.toComposeKeyboardType(),
                imeAction = ImeAction.Next
            ),
            singleLine = true,
            shape = RoundedCornerShape(AppTheme.radius.radius12),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = AppTheme.colors.inputBackground,
                unfocusedContainerColor = AppTheme.colors.inputBackground,
                errorContainerColor = AppTheme.colors.inputBackground,
                focusedBorderColor = AppTheme.colors.primaryAccent,
                unfocusedBorderColor = Color.Transparent,
                errorBorderColor = AppTheme.colors.error,
                focusedTextColor = AppTheme.colors.textPrimary,
                unfocusedTextColor = AppTheme.colors.textPrimary,
                errorTextColor = AppTheme.colors.textPrimary,
                cursorColor = AppTheme.colors.primaryAccent
            ),
            textStyle = AppTheme.typography.bodyLarge
        )

        if (error != null) {
            Text(
                text = error,
                style = AppTheme.typography.bodyMedium,
                color = AppTheme.colors.error,
                modifier = Modifier.padding(start = AppTheme.spacing.spacing8, top = AppTheme.spacing.spacing4)
            )
        }
    }
}

private fun KeyboardType.toComposeKeyboardType(): ComposeKeyboardType {
    return when (this) {
        KeyboardType.TEXT -> ComposeKeyboardType.Text
        KeyboardType.NUMBER -> ComposeKeyboardType.Number
        KeyboardType.EMAIL -> ComposeKeyboardType.Email
    }
}
