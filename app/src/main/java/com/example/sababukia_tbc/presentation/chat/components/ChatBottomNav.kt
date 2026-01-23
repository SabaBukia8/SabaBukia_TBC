package com.example.sababukia_tbc.presentation.chat.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.sababukia_tbc.R
import com.example.sababukia_tbc.ui.theme.ChatBackground
import com.example.sababukia_tbc.ui.theme.Spacing

@Composable
fun ChatBottomNav(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(ChatBackground)
            .padding(vertical = Spacing.spacer16),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { }) {
            Icon(
                painter = painterResource(id = R.drawable.ic_heart),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier.size(Spacing.spacer16)
            )
        }

        IconButton(onClick = { }) {
            Icon(
                painter = painterResource(id = R.drawable.ic_home),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier.size(Spacing.spacer16)
            )
        }

        IconButton(onClick = { }) {
            Icon(
                painter = painterResource(id = R.drawable.ic_messages),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier.size(Spacing.spacer16)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ChatBottomNavPreview() {
    ChatBottomNav()
}
