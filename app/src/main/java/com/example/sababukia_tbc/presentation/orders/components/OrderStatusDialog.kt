package com.example.sababukia_tbc.presentation.orders.components

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.window.Dialog
import com.example.sababukia_tbc.domain.model.Order
import com.example.sababukia_tbc.domain.model.OrderStatus
import com.example.sababukia_tbc.ui.theme.CanceledText
import com.example.sababukia_tbc.ui.theme.DeliveredText
import com.example.sababukia_tbc.ui.theme.Radius
import com.example.sababukia_tbc.ui.theme.Spacing
import com.example.sababukia_tbc.ui.theme.White

@Composable
fun OrderStatusDialog(
    order: Order,
    onDismiss: () -> Unit,
    onStatusChange: (OrderStatus) -> Unit,
    modifier: Modifier = Modifier
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = modifier.fillMaxWidth(),
            shape = RoundedCornerShape(Radius.radius16),
            colors = CardDefaults.cardColors(containerColor = White)
        ) {
            Column(
                modifier = Modifier.padding(Spacing.spacer24),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Update Order Status",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(Spacing.spacer8))

                Text(
                    text = "Order #${order.orderNumber}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(Spacing.spacer24))

                Text(
                    text = "Change status to:",
                    style = MaterialTheme.typography.bodyLarge
                )

                Spacer(modifier = Modifier.height(Spacing.spacer16))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.spacer12)
                ) {
                    Button(
                        onClick = { onStatusChange(OrderStatus.DELIVERED) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DeliveredText
                        ),
                        shape = RoundedCornerShape(Radius.radius8)
                    ) {
                        Text("Delivered")
                    }

                    Button(
                        onClick = { onStatusChange(OrderStatus.CANCELED) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CanceledText
                        ),
                        shape = RoundedCornerShape(Radius.radius8)
                    ) {
                        Text("Cancel")
                    }
                }

                Spacer(modifier = Modifier.height(Spacing.spacer16))

                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(Radius.radius8)
                ) {
                    Text("Close")
                }
            }
        }
    }
}
