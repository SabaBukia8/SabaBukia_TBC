package screen.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sababukia_tbc.R
import data.OrderConstants
import data.SeedData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import model.Order
import model.OrderStatus

class OrdersViewModel : ViewModel() {


    private val _orders = MutableStateFlow(seedOrders())

    fun ordersFor(status: OrderStatus): StateFlow<List<Order>> = _orders
        .map { list ->
            list.filter { it.status == status }
                .sortedByDescending { it.dateMillis }
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(OrderConstants.STATE_FLOW_TIMEOUT_MS),
            emptyList()
        )

    fun getOrder(id: Long): StateFlow<Order?> = _orders
        .map { list -> list.firstOrNull { it.id == id } }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(OrderConstants.STATE_FLOW_TIMEOUT_MS),
            null
        )

    fun submitReview(id: Long, rating: Int, text: String) {
        _orders.value = _orders.value.map { order ->
            if (order.id == id) order.copy(rating = rating, reviewText = text)
            else order
        }
    }

    private fun seedOrders(): List<Order> {
        val now = System.currentTimeMillis()
        fun order(
            offsetDays: Int,
            title: String,
            colorName: String,
            colorArgb: Int,
            imageResId: Int,
            quantity: Int,
            subtotalCents: Int,
            status: OrderStatus
        ): Order = Order(
            id = now - offsetDays * OrderConstants.MILLIS_PER_DAY,
            title = title,
            colorName = colorName,
            colorArgb = colorArgb,
            imageResId = imageResId,
            quantity = quantity,
            subtotalCents = subtotalCents,
            dateMillis = now - offsetDays * OrderConstants.MILLIS_PER_DAY,
            status = status
        )
        return listOf(
            order(
                1,
                SeedData.TITLE_MODERN_WINGBACK,
                SeedData.COLOR_BLACK,
                R.color.black,
                R.mipmap.chair_black_foreground,
                2,
                28000,
                OrderStatus.COMPLETED
            ),
            order(
                2,
                SeedData.TITLE_WOODEN_CHAIR,
                SeedData.COLOR_BROWN,
                R.color.brown,
                R.mipmap.chair_wooden_foreground,
                3,
                14000,
                OrderStatus.COMPLETED
            ),
            order(
                3,
                SeedData.TITLE_MIRRORED_REFLECTOR,
                SeedData.COLOR_BLACK,
                R.color.black,
                R.mipmap.lamp_black_foreground,
                1,
                9000,
                OrderStatus.COMPLETED
            ),
            order(
                4,
                SeedData.TITLE_MINI_BOOKSHELF,
                SeedData.COLOR_BROWN,
                R.color.brown,
                R.mipmap.lamp_orange_foreground,
                1,
                14358,
                OrderStatus.ACTIVE
            ),
            order(
                5,
                SeedData.TITLE_DESK_LAMP,
                SeedData.COLOR_BLACK,
                R.color.black,
                R.mipmap.bookshelf_foreground,
                1,
                12000,
                OrderStatus.ACTIVE
            )
        )
    }
}