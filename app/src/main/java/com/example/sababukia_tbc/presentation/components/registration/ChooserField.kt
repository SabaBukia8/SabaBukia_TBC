package com.example.sababukia_tbc.presentation.components.registration

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.sababukia_tbc.presentation.theme.AppTheme

@Composable
fun ChooserField(
    value: String,
    onClick: () -> Unit,
    hint: String,
    isRequired: Boolean,
    error: String?,
    iconUrl: String?,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick),
            enabled = false,
            readOnly = true,
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
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Select",
                    tint = AppTheme.colors.textSecondary
                )
            },
            isError = error != null,
            singleLine = true,
            shape = RoundedCornerShape(AppTheme.radius.radius12),
            colors = OutlinedTextFieldDefaults.colors(
                disabledContainerColor = AppTheme.colors.inputBackground,
                disabledBorderColor = if (error != null) AppTheme.colors.error else Color.Transparent,
                disabledTextColor = AppTheme.colors.textPrimary,
                disabledLabelColor = AppTheme.colors.textSecondary
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
