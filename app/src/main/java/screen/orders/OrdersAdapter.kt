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
import data.OrderConstants
import model.Order
import model.OrderStatus
import java.util.Locale

class OrdersAdapter(
    private val onDetailsClick: (Order) -> Unit = {},
    private val onReviewClick: (Order) -> Unit = {},
    private val onBuyAgainClick: (Order) -> Unit = {}
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
            ivProduct.setImageResource(order.imageResId)
            tvTitle.text = order.title
            tvColorQty.text = itemView.context.getString(R.string.qty_equals, order.quantity)

            vColorDot.backgroundTintList = ColorStateList.valueOf(order.colorArgb)
            tvColorName.text = order.colorName

            tvStatus.text = when (order.status) {
                OrderStatus.ACTIVE -> itemView.context.getString(R.string.status_active)
                OrderStatus.COMPLETED -> itemView.context.getString(R.string.status_completed)
            }
            val statusColor = when (order.status) {
                OrderStatus.ACTIVE -> R.color.orange
                OrderStatus.COMPLETED -> R.color.green
            }
            tvStatus.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(itemView.context, statusColor))

            tvPrice.text = formatPrice(order.subtotalCents)

            btnPrimary.text = when {
                order.status == OrderStatus.COMPLETED && order.rating == null -> itemView.context.getString(
                    R.string.action_leave_review
                )

                order.status == OrderStatus.COMPLETED && order.rating != null -> itemView.context.getString(
                    R.string.action_buy_again
                )

                else -> itemView.context.getString(R.string.action_details)
            }
            btnPrimary.setOnClickListener {
                when (btnPrimary.text) {
                    itemView.context.getString(R.string.action_leave_review) -> onReviewClick(order)
                    itemView.context.getString(R.string.action_buy_again) -> onBuyAgainClick(order)
                    else -> onDetailsClick(order)
                }
            }
        }
    }

    private fun formatPrice(cents: Int): String {
        val dollars = cents.toDouble() / OrderConstants.CENTS_PER_DOLLAR
        return String.format(Locale.US, "$%.${OrderConstants.PRICE_DECIMAL_PLACES}f", dollars)
    }

    private class Diff : DiffUtil.ItemCallback<Order>() {
        override fun areItemsTheSame(oldItem: Order, newItem: Order): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Order, newItem: Order): Boolean =
            oldItem == newItem
    }
}