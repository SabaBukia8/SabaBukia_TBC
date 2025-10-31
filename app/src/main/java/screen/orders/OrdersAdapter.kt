package screen.orders

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.sababukia_tbc.R
import com.example.sababukia_tbc.databinding.ItemOrderBinding
import model.Order
import model.OrderStatus

class OrdersAdapter(
    private val onDetailsClick: (Order) -> Unit
) : ListAdapter<Order, OrdersAdapter.VH>(Diff()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position))
    }

    inner class VH(private val binding: ItemOrderBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(order: Order) = with(binding) {
            tvStatus.text = when (order.status) {
                OrderStatus.PENDING -> itemView.context.getString(R.string.status_pending)
                OrderStatus.DELIVERED -> itemView.context.getString(R.string.status_delivered)
                OrderStatus.CANCELLED -> itemView.context.getString(R.string.status_cancelled)
            }

            val colorRes = when (order.status) {
                OrderStatus.PENDING -> R.color.orange
                OrderStatus.DELIVERED -> R.color.green
                OrderStatus.CANCELLED -> R.color.red
            }
            val color = ContextCompat.getColor(itemView.context, colorRes)
            tvStatus.backgroundTintList = ColorStateList.valueOf(color)

            tvOrderTitle.text = "Order #${order.id}"
            tvTrackingNumber.text = order.trackingNumber
            tvQuantity.text = order.quantity.toString()
            tvSubtotal.text = formatPrice(order.subtotalCents)
            tvDate.text = android.text.format.DateFormat.format("dd/MM/yyyy", order.dateMillis)

            btnDetails.setOnClickListener { onDetailsClick(order) }
        }
    }

    private fun formatPrice(cents: Int): String {
        val dollars = cents / 100
        val remainder = cents % 100
        return "$${dollars}${
            if (remainder > 0) ".${
                remainder.toString().padStart(2, '0')
            }" else ""
        }"
    }

    private class Diff : DiffUtil.ItemCallback<Order>() {
        override fun areItemsTheSame(oldItem: Order, newItem: Order): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Order, newItem: Order): Boolean =
            oldItem == newItem
    }
}
