package com.example.sababukia_tbc.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

object AppTheme {
    val colors: AppColorScheme
        @Composable
        get() = LocalAppColorScheme.current

    val typography: AppTypography
        @Composable
        get() = LocalAppTypography.current

    val spacing: Spacing
        @Composable
        get() = LocalSpacing.current

    val radius: Radius
        @Composable
        get() = LocalRadius.current

    val elevation: Elevation
        @Composable
        get() = LocalElevation.current
}

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    CompositionLocalProvider(
        LocalAppColorScheme provides colorScheme,
        LocalAppTypography provides DefaultAppTypography,
        LocalSpacing provides DefaultSpacing,
        LocalRadius provides DefaultRadius,
        LocalElevation provides DefaultElevation,
        content = content
    )
}
