package com.example.sababukia_tbc.data.mapper

import com.example.sababukia_tbc.data.remote.dto.OrderDto
import com.example.sababukia_tbc.domain.model.Order
import com.example.sababukia_tbc.domain.model.OrderStatus

fun OrderDto.toDomain(): Order = Order(
    id = id,
    orderNumber = orderNumber,
    date = date,
    trackingNumber = trackingNumber,
    quantity = quantity,
    subtotal = subtotal,
    status = when (status.uppercase()) {
        "DELIVERED" -> OrderStatus.DELIVERED
        "CANCELED", "CANCELLED" -> OrderStatus.CANCELED
        else -> OrderStatus.PENDING
    }
)

fun Order.toDto(): OrderDto = OrderDto(
    id = id,
    orderNumber = orderNumber,
    date = date,
    trackingNumber = trackingNumber,
    quantity = quantity,
    subtotal = subtotal,
    status = status.name
)
