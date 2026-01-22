package com.example.sababukia_tbc.data.remote

import com.example.sababukia_tbc.data.remote.dto.OrderDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface OrderApi {
    @GET("orders")
    suspend fun getOrders(): List<OrderDto>

    @PUT("orders/{id}")
    suspend fun updateOrderStatus(
        @Path("id") id: Int,
        @Body order: OrderDto
    ): OrderDto
}
