package com.example.sababukia_tbc.presentation.components.registration

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.sababukia_tbc.presentation.theme.AppTheme

@Composable
fun GenderPickerDialog(
    onDismiss: () -> Unit,
    onGenderSelected: (String) -> Unit
) {
    val genderOptions = listOf("Male", "Female", "Other")
    var selectedGender by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Select Gender",
                style = AppTheme.typography.titleMedium,
                color = AppTheme.colors.textPrimary
            )
        },
        text = {
            Column {
                genderOptions.forEach { gender ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedGender = gender }
                            .padding(vertical = AppTheme.spacing.spacing8),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedGender == gender,
                            onClick = { selectedGender = gender },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = AppTheme.colors.primaryAccent,
                                unselectedColor = AppTheme.colors.textSecondary
                            )
                        )
                        Spacer(modifier = Modifier.width(AppTheme.spacing.spacing8))
                        Text(
                            text = gender,
                            style = AppTheme.typography.bodyLarge,
                            color = AppTheme.colors.textPrimary
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    selectedGender?.let { onGenderSelected(it) }
                },
                enabled = selectedGender != null
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        shape = RoundedCornerShape(AppTheme.radius.radius12),
        containerColor = AppTheme.colors.cardBackground
    )
}
