package com.example.sababukia_tbc.presentation.components.registration

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.sababukia_tbc.presentation.theme.AppTheme

@Composable
fun RegistrationButton(
    text: String,
    onClick: () -> Unit,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        enabled = !isLoading,
        shape = RoundedCornerShape(AppTheme.radius.radius12),
        colors = ButtonDefaults.buttonColors(
            containerColor = AppTheme.colors.primaryAccent,
            contentColor = AppTheme.colors.textPrimary,
            disabledContainerColor = AppTheme.colors.primaryAccent.copy(alpha = 0.6f),
            disabledContentColor = AppTheme.colors.textPrimary.copy(alpha = 0.6f)
        )
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = AppTheme.colors.textPrimary,
                strokeWidth = 2.dp
            )
        } else {
            Text(
                text = text,
                style = AppTheme.typography.labelMedium
            )
        }
    }
}
