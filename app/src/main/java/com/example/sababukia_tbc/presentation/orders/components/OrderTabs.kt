package com.example.sababukia_tbc.presentation.orders.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.example.sababukia_tbc.presentation.orders.OrderTab
import com.example.sababukia_tbc.ui.theme.LightGray
import com.example.sababukia_tbc.ui.theme.Radius
import com.example.sababukia_tbc.ui.theme.Spacing

@Composable
fun OrderTabs(
    selectedTab: OrderTab,
    onTabSelected: (OrderTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.spacer16, vertical = Spacing.spacer8),
        horizontalArrangement = Arrangement.spacedBy(Spacing.spacer8)
    ) {
        OrderTab.entries.forEach { tab ->
            TabChip(
                text = tab.displayName(),
                isSelected = tab == selectedTab,
                onClick = { onTabSelected(tab) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun TabChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (isSelected) {
        MaterialTheme.colorScheme.primary
    } else {
        LightGray
    }
    val textColor = if (isSelected) {
        Color.White
    } else {
        Color.Black
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(Radius.radius20))
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(vertical = Spacing.spacer10, horizontal = Spacing.spacer16),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}

private fun OrderTab.displayName(): String = when (this) {
    OrderTab.PENDING -> "Pending"
    OrderTab.DELIVERED -> "Delivered"
    OrderTab.CANCELED -> "Cancelled"
}
