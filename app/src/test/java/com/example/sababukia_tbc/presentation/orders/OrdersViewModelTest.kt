package com.example.sababukia_tbc.presentation.orders

import app.cash.turbine.test
import com.example.sababukia_tbc.domain.model.Order
import com.example.sababukia_tbc.domain.model.OrderError
import com.example.sababukia_tbc.domain.model.OrderStatus
import com.example.sababukia_tbc.domain.model.Result
import com.example.sababukia_tbc.domain.repository.OrderRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class OrdersViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var orderRepository: OrderRepository
    private lateinit var viewModel: OrdersViewModel

    private val sampleOrders = listOf(
        Order(
            id = 1,
            orderNumber = "1001",
            date = "01/01/2025",
            trackingNumber = "TRK001",
            quantity = 2,
            subtotal = 100.0,
            status = OrderStatus.PENDING
        ),
        Order(
            id = 2,
            orderNumber = "1002",
            date = "02/01/2025",
            trackingNumber = "TRK002",
            quantity = 1,
            subtotal = 50.0,
            status = OrderStatus.DELIVERED
        ),
        Order(
            id = 3,
            orderNumber = "1003",
            date = "03/01/2025",
            trackingNumber = "TRK003",
            quantity = 3,
            subtotal = 150.0,
            status = OrderStatus.CANCELED
        )
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        orderRepository = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init should load orders successfully`() = runTest {
        coEvery { orderRepository.getOrders() } returns Result.Success(sampleOrders)

        viewModel = OrdersViewModel(orderRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals(sampleOrders, state.orders)
        assertFalse(state.isLoading)
        assertEquals(null, state.error)
    }

    @Test
    fun `init should handle network error`() = runTest {
        coEvery { orderRepository.getOrders() } returns Result.Error(OrderError.Network)

        viewModel = OrdersViewModel(orderRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.state.value
        assertTrue(state.orders.isEmpty())
        assertFalse(state.isLoading)
        assertEquals("Network error. Please check your connection.", state.error)
    }

    @Test
    fun `selectTab should filter orders by status`() = runTest {
        coEvery { orderRepository.getOrders() } returns Result.Success(sampleOrders)

        viewModel = OrdersViewModel(orderRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(OrderTab.PENDING, viewModel.state.value.selectedTab)
        assertEquals(1, viewModel.state.value.filteredOrders.size)
        assertEquals(OrderStatus.PENDING, viewModel.state.value.filteredOrders.first().status)

        viewModel.onEvent(OrdersEvent.SelectTab(OrderTab.DELIVERED))
        assertEquals(OrderTab.DELIVERED, viewModel.state.value.selectedTab)
        assertEquals(1, viewModel.state.value.filteredOrders.size)
        assertEquals(OrderStatus.DELIVERED, viewModel.state.value.filteredOrders.first().status)

        viewModel.onEvent(OrdersEvent.SelectTab(OrderTab.CANCELED))
        assertEquals(OrderTab.CANCELED, viewModel.state.value.selectedTab)
        assertEquals(1, viewModel.state.value.filteredOrders.size)
        assertEquals(OrderStatus.CANCELED, viewModel.state.value.filteredOrders.first().status)
    }

    @Test
    fun `updateStatus should update PENDING to DELIVERED`() = runTest {
        coEvery { orderRepository.getOrders() } returns Result.Success(sampleOrders)
        val updatedOrder = sampleOrders[0].copy(status = OrderStatus.DELIVERED)
        coEvery { orderRepository.updateOrderStatus(any()) } returns Result.Success(updatedOrder)

        viewModel = OrdersViewModel(orderRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onEvent(OrdersEvent.OpenDialog(sampleOrders[0]))
        viewModel.onEvent(OrdersEvent.UpdateStatus(OrderStatus.DELIVERED))
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.state.value
        val order = state.orders.find { it.id == 1 }
        assertEquals(OrderStatus.DELIVERED, order?.status)
        assertFalse(state.isUpdating)
    }

    @Test
    fun `updateStatus should emit success side effect`() = runTest {
        coEvery { orderRepository.getOrders() } returns Result.Success(sampleOrders)
        val updatedOrder = sampleOrders[0].copy(status = OrderStatus.DELIVERED)
        coEvery { orderRepository.updateOrderStatus(any()) } returns Result.Success(updatedOrder)

        viewModel = OrdersViewModel(orderRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.sideEffect.test {
            viewModel.onEvent(OrdersEvent.OpenDialog(sampleOrders[0]))
            viewModel.onEvent(OrdersEvent.UpdateStatus(OrderStatus.DELIVERED))
            testDispatcher.scheduler.advanceUntilIdle()

            val effect = awaitItem()
            assertTrue(effect is OrdersSideEffect.StatusUpdateSuccess)
            assertEquals(
                OrderStatus.DELIVERED,
                (effect as OrdersSideEffect.StatusUpdateSuccess).newStatus
            )
        }
    }

    @Test
    fun `refresh should reload orders`() = runTest {
        coEvery { orderRepository.getOrders() } returns Result.Success(sampleOrders)

        viewModel = OrdersViewModel(orderRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        val newOrders = sampleOrders + Order(
            id = 4,
            orderNumber = "1004",
            date = "04/01/2025",
            trackingNumber = "TRK004",
            quantity = 4,
            subtotal = 200.0,
            status = OrderStatus.PENDING
        )
        coEvery { orderRepository.getOrders() } returns Result.Success(newOrders)

        viewModel.onEvent(OrdersEvent.Refresh)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals(4, state.orders.size)
        assertFalse(state.isRefreshing)
    }
}
