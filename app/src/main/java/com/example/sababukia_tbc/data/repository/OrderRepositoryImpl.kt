package com.example.sababukia_tbc.data.repository

import com.example.sababukia_tbc.data.common.safeCall
import com.example.sababukia_tbc.data.mapper.toDomain
import com.example.sababukia_tbc.data.mapper.toDto
import com.example.sababukia_tbc.data.remote.OrderApi
import com.example.sababukia_tbc.domain.model.Order
import com.example.sababukia_tbc.domain.model.OrderError
import com.example.sababukia_tbc.domain.model.Result
import com.example.sababukia_tbc.domain.repository.OrderRepository
import java.io.IOException
import javax.inject.Inject

class OrderRepositoryImpl @Inject constructor(
    private val api: OrderApi
) : OrderRepository {

    override suspend fun getOrders(): Result<List<Order>, OrderError> = safeCall(
        exceptionMapper = ::mapException
    ) {
        api.getOrders().map { it.toDomain() }
    }

    override suspend fun updateOrderStatus(order: Order): Result<Order, OrderError> = safeCall(
        exceptionMapper = ::mapException
    ) {
        api.updateOrderStatus(order.id, order.toDto()).toDomain()
    }

    private fun mapException(e: Exception): OrderError = when (e) {
        is IOException -> OrderError.Network
        else -> OrderError.Unknown
    }
}
