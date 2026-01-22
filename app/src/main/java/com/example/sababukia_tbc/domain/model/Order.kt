package com.example.sababukia_tbc.domain.model

data class Order(
    val id: Int,
    val orderNumber: String,
    val date: String,
    val trackingNumber: String,
    val quantity: Int,
    val subtotal: Double,
    val status: OrderStatus
) {
    fun canChangeStatus(): Boolean = status == OrderStatus.PENDING
}

enum class OrderStatus {
    PENDING,
    DELIVERED,
    CANCELED
}
