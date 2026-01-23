package com.example.sababukia_tbc.presentation.orders.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.example.sababukia_tbc.domain.model.Order
import com.example.sababukia_tbc.domain.model.OrderStatus
import com.example.sababukia_tbc.ui.theme.CanceledBg
import com.example.sababukia_tbc.ui.theme.CanceledText
import com.example.sababukia_tbc.ui.theme.DeliveredBg
import com.example.sababukia_tbc.ui.theme.DeliveredText
import com.example.sababukia_tbc.ui.theme.OrderCardBg
import com.example.sababukia_tbc.ui.theme.PendingBg
import com.example.sababukia_tbc.ui.theme.PendingText
import com.example.sababukia_tbc.ui.theme.Radius
import com.example.sababukia_tbc.ui.theme.Spacing

@Composable
fun OrderCard(
    order: Order,
    onDetailsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Radius.radius12),
        colors = CardDefaults.cardColors(containerColor = OrderCardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = Spacing.spacer2)
    ) {
        Column(
            modifier = Modifier.padding(Spacing.spacer16)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Order #${order.orderNumber}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = order.date,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(Spacing.spacer8))

            Text(
                text = "Tracking: ${order.trackingNumber}",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(Spacing.spacer8))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Quantity: ${order.quantity}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Subtotal: $${String.format("%.2f", order.subtotal)}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(Spacing.spacer12))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatusBadge(status = order.status)

                Button(
                    onClick = onDetailsClick,
                    enabled = order.canChangeStatus(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        disabledContainerColor = Color.LightGray
                    ),
                    shape = RoundedCornerShape(Radius.radius8)
                ) {
                    Text("Details")
                }
            }
        }
    }
}

@Composable
private fun StatusBadge(
    status: OrderStatus,
    modifier: Modifier = Modifier
) {
    val (backgroundColor, textColor, text) = when (status) {
        OrderStatus.PENDING -> Triple(PendingBg, PendingText, "PENDING")
        OrderStatus.DELIVERED -> Triple(DeliveredBg, DeliveredText, "DELIVERED")
        OrderStatus.CANCELED -> Triple(CanceledBg, CanceledText, "CANCELLED")
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(Radius.radius6))
            .background(backgroundColor)
            .padding(horizontal = Spacing.spacer12, vertical = Spacing.spacer4)
    ) {
        Text(
            text = text,
            color = textColor,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold
        )
    }
}
