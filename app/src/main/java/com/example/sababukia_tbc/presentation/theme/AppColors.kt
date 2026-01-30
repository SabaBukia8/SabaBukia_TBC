package com.example.sababukia_tbc.presentation.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

@Immutable
data class AppColorScheme(
    val backgroundGradientStart: Color,
    val backgroundGradientEnd: Color,
    val cardBackground: Color,
    val inputBackground: Color,
    val primaryAccent: Color,
    val error: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val separator: Color,
    val shadow: Color
) {
    val backgroundGradient: Brush
        get() = Brush.linearGradient(
            colors = listOf(backgroundGradientStart, backgroundGradientEnd)
        )
}

val DarkColorScheme = AppColorScheme(
    backgroundGradientStart = Color(0xFF22343C),
    backgroundGradientEnd = Color(0xFF1F2E35),
    cardBackground = Color(0xFF30444E),
    inputBackground = Color(0xFF2A3C44),
    primaryAccent = Color(0xFF3ED598),
    error = Color(0xFFFF565E),
    textPrimary = Color(0xFFFFFFFF),
    textSecondary = Color(0xFF96A7AF),
    separator = Color(0xFFB8C2C0).copy(alpha = 0.35f),
    shadow = Color(0xFF19282F)
)

val LightColorScheme = AppColorScheme(
    backgroundGradientStart = Color(0xFFF5F5F5),
    backgroundGradientEnd = Color(0xFFE8E8E8),
    cardBackground = Color(0xFFFFFFFF),
    inputBackground = Color(0xFFF0F0F0),
    primaryAccent = Color(0xFF3ED598),
    error = Color(0xFFFF565E),
    textPrimary = Color(0xFF1A1A1A),
    textSecondary = Color(0xFF666666),
    separator = Color(0xFF333333).copy(alpha = 0.15f),
    shadow = Color(0xFF000000).copy(alpha = 0.1f)
)

val LocalAppColorScheme = staticCompositionLocalOf { DarkColorScheme }
