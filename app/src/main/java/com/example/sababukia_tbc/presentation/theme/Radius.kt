package com.example.sababukia_tbc.presentation.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class Radius(
    val radius12: Dp = 12.dp,
    val radius25: Dp = 25.dp
)

val DefaultRadius = Radius()

val LocalRadius = staticCompositionLocalOf { DefaultRadius }
