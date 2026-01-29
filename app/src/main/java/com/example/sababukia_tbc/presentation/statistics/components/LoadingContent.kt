package com.example.sababukia_tbc.presentation.statistics.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.sababukia_tbc.ui.theme.AppColors
import com.example.sababukia_tbc.ui.theme.ApplicationTheme

@Composable
fun LoadingContent(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = AppColors.current.primary
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LoadingContentPreview() {
    ApplicationTheme {
        LoadingContent()
    }
}
