package com.example.sababukia_tbc.presentation.chat.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import com.example.sababukia_tbc.ui.theme.AvatarCoral
import com.example.sababukia_tbc.ui.theme.AvatarGreen
import com.example.sababukia_tbc.ui.theme.AvatarYellow
import com.example.sababukia_tbc.ui.theme.Spacing
import com.example.sababukia_tbc.ui.theme.White
import kotlin.math.absoluteValue

@Composable
fun AvatarPlaceholder(
    name: String,
    size: Dp = Spacing.spacer48,
    modifier: Modifier = Modifier
) {
    val colors = listOf(AvatarYellow, AvatarGreen, AvatarCoral)
    val colorIndex = name.hashCode().absoluteValue % colors.size
    val backgroundColor = colors[colorIndex]
    val initial = name.firstOrNull()?.uppercase() ?: "?"

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initial,
            color = White,
            fontWeight = FontWeight.Bold,
            fontSize = (size.value / 2).sp
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF1A2F2A)
@Composable
private fun AvatarPlaceholderPreview() {
    Row {
        AvatarPlaceholder(name = "John")
        Spacer(modifier = Modifier.width(Spacing.spacer8))
        AvatarPlaceholder(name = "Alice")
        Spacer(modifier = Modifier.width(Spacing.spacer8))
        AvatarPlaceholder(name = "Bob")
    }
}
