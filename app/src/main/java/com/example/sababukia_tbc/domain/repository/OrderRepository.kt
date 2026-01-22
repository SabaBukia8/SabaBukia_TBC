package com.example.sababukia_tbc.domain.repository

import com.example.sababukia_tbc.domain.model.Order
import com.example.sababukia_tbc.domain.model.OrderError
import com.example.sababukia_tbc.domain.model.Result

interface OrderRepository {
    suspend fun getOrders(): Result<List<Order>, OrderError>
    suspend fun updateOrderStatus(order: Order): Result<Order, OrderError>
}
