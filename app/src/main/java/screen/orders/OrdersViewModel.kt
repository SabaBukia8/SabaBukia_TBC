package screen.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import data.OrdersRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import model.Order
import model.OrderStatus

class OrdersViewModel(
    private val repository: OrdersRepository = OrdersRepository
) : ViewModel() {

    private val _selectedStatus = MutableStateFlow(OrderStatus.PENDING)
    val selectedStatus: StateFlow<OrderStatus> = _selectedStatus

    val ordersByStatus: StateFlow<List<Order>> = combine(
        repository.orders,
        _selectedStatus
    ) { orders, status ->
        orders.filter { it.status == status }
            .sortedWith(compareBy<Order> { it.dateMillis }.reversed())
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    fun setStatus(status: OrderStatus) {
        _selectedStatus.value = status
    }

    fun getOrder(id: Long): StateFlow<Order?> = repository.orders
        .map { list -> list.firstOrNull { it.id == id } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    fun markDelivered(id: Long) {
        repository.updateStatus(id, OrderStatus.DELIVERED)
        _selectedStatus.value = OrderStatus.DELIVERED
    }

    fun markCancelled(id: Long) {
        repository.updateStatus(id, OrderStatus.CANCELLED)
        _selectedStatus.value = OrderStatus.CANCELLED
    }
}
