package com.example.sababukia_tbc.presentation.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class Spacing(
    val spacing4: Dp = 4.dp,
    val spacing8: Dp = 8.dp,
    val spacing12: Dp = 12.dp,
    val spacing16: Dp = 16.dp,
    val spacing19: Dp = 19.dp,
    val spacing24: Dp = 24.dp,
    val spacing32: Dp = 32.dp
)

val DefaultSpacing = Spacing()

val LocalSpacing = staticCompositionLocalOf { DefaultSpacing }
