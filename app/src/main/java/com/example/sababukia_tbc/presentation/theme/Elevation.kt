package com.example.sababukia_tbc.presentation.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class Elevation(
    val elevation14: Dp = 14.dp
)

val DefaultElevation = Elevation()

val LocalElevation = staticCompositionLocalOf { DefaultElevation }
