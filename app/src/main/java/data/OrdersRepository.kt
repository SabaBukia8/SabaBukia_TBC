package data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import model.Order
import model.OrderStatus

object OrdersRepository {

    private val _orders = MutableStateFlow(seedOrders())
    val orders: StateFlow<List<Order>> = _orders.asStateFlow()


    fun updateStatus(id: Long, status: OrderStatus) {
        _orders.value = _orders.value.map { order ->
            if (order.id == id) order.copy(status = status) else order
        }
    }

    private fun seedOrders(): List<Order> {
        val now = System.currentTimeMillis()
        fun order(i: Int, quantity: Int, subtotal: Int): Order = Order(
            id = i.toLong(),
            trackingNumber = System.currentTimeMillis().toString(),
            quantity = quantity,
            subtotalCents = subtotal * 100,
            dateMillis = now - i * 86_400_000L,
            status = OrderStatus.PENDING
        )
        return listOf(
            order(1829, 2, 210),
            order(1824, 3, 120),
            order(1679, 3, 450),
            order(1671, 3, 400),
            order(1514, 2, 110),
            order(1524, 2, 110),
        )
    }
}
