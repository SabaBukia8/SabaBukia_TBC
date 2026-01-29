package com.example.sababukia_tbc.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

@Composable
fun ApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) AppColorScheme.dark() else AppColorScheme.light()
    CompositionLocalProvider(LocalAppColors provides colors) {
        content()
    }
}
