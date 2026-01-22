package com.example.sababukia_tbc.presentation.orders

import androidx.lifecycle.viewModelScope
import com.example.sababukia_tbc.domain.model.Order
import com.example.sababukia_tbc.domain.model.OrderError
import com.example.sababukia_tbc.domain.model.OrderStatus
import com.example.sababukia_tbc.domain.repository.OrderRepository
import com.example.sababukia_tbc.presentation.common.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrdersViewModel @Inject constructor(
    private val orderRepository: OrderRepository
) : BaseViewModel<OrdersState, OrdersEvent, OrdersSideEffect>(OrdersState()) {

    init {
        loadOrders()
    }

    override fun onEvent(event: OrdersEvent) {
        when (event) {
            is OrdersEvent.SelectTab -> selectTab(event.tab)
            is OrdersEvent.Refresh -> refresh()
            is OrdersEvent.OpenDialog -> openDialog(event.order)
            is OrdersEvent.DismissDialog -> dismissDialog()
            is OrdersEvent.UpdateStatus -> updateStatus(event.newStatus)
            is OrdersEvent.DismissError -> dismissError()
        }
    }

    private fun loadOrders() {
        viewModelScope.launch {
            updateState { copy(isLoading = true, error = null) }
            orderRepository.getOrders()
                .onSuccess { orders ->
                    updateState { copy(orders = orders, isLoading = false) }
                }
                .onError { error ->
                    updateState {
                        copy(
                            isLoading = false,
                            error = error.toMessage()
                        )
                    }
                }
        }
    }

    private fun selectTab(tab: OrderTab) {
        updateState { copy(selectedTab = tab) }
    }

    private fun refresh() {
        viewModelScope.launch {
            updateState { copy(isRefreshing = true, error = null) }
            orderRepository.getOrders()
                .onSuccess { orders ->
                    updateState { copy(orders = orders, isRefreshing = false) }
                }
                .onError { error ->
                    updateState {
                        copy(
                            isRefreshing = false,
                            error = error.toMessage()
                        )
                    }
                }
        }
    }

    private fun openDialog(order: Order) {
        if (order.canChangeStatus()) {
            updateState { copy(selectedOrder = order, showDialog = true) }
        }
    }

    private fun dismissDialog() {
        updateState { copy(selectedOrder = null, showDialog = false) }
    }

    private fun updateStatus(newStatus: OrderStatus) {
        val order = currentState.selectedOrder ?: return
        viewModelScope.launch {
            updateState { copy(isUpdating = true, showDialog = false) }
            val updatedOrder = order.copy(status = newStatus)
            orderRepository.updateOrderStatus(updatedOrder)
                .onSuccess { result ->
                    updateState {
                        copy(
                            orders = orders.map { if (it.id == result.id) result else it },
                            isUpdating = false,
                            selectedOrder = null
                        )
                    }
                    sendSideEffect(OrdersSideEffect.StatusUpdateSuccess(newStatus))
                }
                .onError { error ->
                    updateState {
                        copy(
                            isUpdating = false,
                            selectedOrder = null,
                            error = error.toMessage()
                        )
                    }
                }
        }
    }

    private fun dismissError() {
        updateState { copy(error = null) }
    }

    private fun OrderError.toMessage(): String = when (this) {
        OrderError.Network -> "Network error. Please check your connection."
        OrderError.Unknown -> "An unexpected error occurred."
    }
}

data class OrdersState(
    val orders: List<Order> = emptyList(),
    val selectedTab: OrderTab = OrderTab.PENDING,
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val isUpdating: Boolean = false,
    val error: String? = null,
    val selectedOrder: Order? = null,
    val showDialog: Boolean = false
) {
    val filteredOrders: List<Order>
        get() = orders.filter { it.status == selectedTab.toOrderStatus() }
}

enum class OrderTab {
    PENDING, DELIVERED, CANCELED;

    fun toOrderStatus(): OrderStatus = when (this) {
        PENDING -> OrderStatus.PENDING
        DELIVERED -> OrderStatus.DELIVERED
        CANCELED -> OrderStatus.CANCELED
    }
}

sealed interface OrdersEvent {
    data class SelectTab(val tab: OrderTab) : OrdersEvent
    data object Refresh : OrdersEvent
    data class OpenDialog(val order: Order) : OrdersEvent
    data object DismissDialog : OrdersEvent
    data class UpdateStatus(val newStatus: OrderStatus) : OrdersEvent
    data object DismissError : OrdersEvent
}

sealed interface OrdersSideEffect {
    data class StatusUpdateSuccess(val newStatus: OrderStatus) : OrdersSideEffect
}
