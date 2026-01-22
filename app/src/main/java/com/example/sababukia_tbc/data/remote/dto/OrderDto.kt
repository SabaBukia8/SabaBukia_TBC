package com.example.sababukia_tbc.data.remote.dto

import com.google.gson.annotations.SerializedName

data class OrderDto(
    val id: Int,
    @SerializedName("order_number") val orderNumber: String,
    val date: String,
    @SerializedName("tracking_number") val trackingNumber: String,
    val quantity: Int,
    val subtotal: Double,
    val status: String
)
