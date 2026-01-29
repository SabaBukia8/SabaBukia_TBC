package com.example.sababukia_tbc.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

data class AppColorScheme(
    val background: Color,
    val backgroundStart: Color,
    val onBackground: Color,
    val onSurfaceVariant: Color,
    val primary: Color,
    val error: Color,
    val cardGreen: Color,
    val cardShadow: Color,
    val badgeText: Color,
    val bottomNavBackground: Color,
    val bottomNavSelected: Color,
    val bottomNavUnselected: Color,
    val cardOverlay: Color
) {
    companion object {
        fun dark() = AppColorScheme(
            background = Color(0xFF1F2E35),
            backgroundStart = Color(0xFF22343C),
            onBackground = Color(0xFFE0E0E0),
            onSurfaceVariant = Color(0xFFA9A9A9),
            primary = Color(0xFF3DD598),
            error = Color(0xFFEF5350),
            cardGreen = Color(0xFF3ED598),
            cardShadow = Color(0xFF19282F),
            badgeText = Color(0xFFFFFFFF),
            bottomNavBackground = Color(0xFF30444E),
            bottomNavSelected = Color(0xFF3DD598),
            bottomNavUnselected = Color(0xFF96A7AF),
            cardOverlay = Color(0x99000000)
        )

        fun light() = AppColorScheme(
            background = Color(0xFFF5F5F5),
            backgroundStart = Color(0xFFFFFFFF),
            onBackground = Color(0xFF1C1B1F),
            onSurfaceVariant = Color(0xFF757575),
            primary = Color(0xFF4169E1),
            error = Color(0xFFF44336),
            cardGreen = Color(0xFF3ED598),
            cardShadow = Color(0xFFD0D0D0),
            badgeText = Color(0xFFFFFFFF),
            bottomNavBackground = Color(0xFFFFFFFF),
            bottomNavSelected = Color(0xFF4169E1),
            bottomNavUnselected = Color(0xFF9E9E9E),
            cardOverlay = Color(0x99000000)
        )
    }
}

val LocalAppColors = staticCompositionLocalOf { AppColorScheme.dark() }

object AppColors {
    val current: AppColorScheme
        @Composable
        get() = LocalAppColors.current
}
