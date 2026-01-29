package com.example.sababukia_tbc.presentation.statistics.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.example.sababukia_tbc.ui.theme.AppColors
import com.example.sababukia_tbc.ui.theme.AppTextStyle
import com.example.sababukia_tbc.ui.theme.ApplicationTheme
import com.example.sababukia_tbc.ui.theme.Spacing

@Composable
fun ErrorContent(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = message,
            style = AppTextStyle.bodyLarge,
            color = AppColors.current.error,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(Spacing.spacer16))
        Button(onClick = onRetry) {
            Text(text = "Retry")
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ErrorContentPreview() {
    ApplicationTheme {
        ErrorContent(
            message = "Network error. Please check your connection.",
            onRetry = {}
        )
    }
}
