package screen.orders

import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import basics.BaseFragment
import com.example.sababukia_tbc.databinding.FragmentOrderDetailsBinding
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import model.OrderStatus

class OrderDetailsFragment :
    BaseFragment<FragmentOrderDetailsBinding>(FragmentOrderDetailsBinding::inflate) {

    private val viewModel: OrdersViewModel by viewModels()
    private val args: OrderDetailsFragmentArgs by navArgs()

    override fun bind() {
        observeOrder()
        setListeners()
    }

    private fun observeOrder() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.getOrder(args.orderId)
                    .filterNotNull()
                    .collect { order ->
                        binding.tvOrderTitle.text = "Order #${order.id}"
                        binding.tvTrackingNumber.text = order.trackingNumber
                        binding.tvQuantity.text = order.quantity.toString()
                        binding.tvSubtotal.text = formatPrice(order.subtotalCents)
                        binding.tvDate.text =
                            android.text.format.DateFormat.format("dd/MM/yyyy", order.dateMillis)

                        val isPending = order.status == OrderStatus.PENDING
                        binding.btnMarkDelivered.isEnabled = isPending
                        binding.btnMarkCancelled.isEnabled = isPending
                    }
            }
        }
    }

    private fun setListeners() {
        binding.btnMarkDelivered.setOnClickListener {
            viewModel.markDelivered(args.orderId)
            findNavController().popBackStack()
        }
        binding.btnMarkCancelled.setOnClickListener {
            viewModel.markCancelled(args.orderId)
            findNavController().popBackStack()
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
}
