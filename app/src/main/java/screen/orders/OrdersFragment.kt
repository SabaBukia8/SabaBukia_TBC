package screen.orders

import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import basics.BaseFragment
import com.example.sababukia_tbc.databinding.FragmentOrdersBinding
import kotlinx.coroutines.launch
import model.OrderStatus

class OrdersFragment : BaseFragment<FragmentOrdersBinding>(FragmentOrdersBinding::inflate) {

    private val viewModel: OrdersViewModel by viewModels()
    private lateinit var adapter: OrdersAdapter

    override fun bind() {
        setupRecycler()
        setupToggle()
        observeState()
    }

    private fun setupRecycler() {
        adapter = OrdersAdapter { order ->
            val action =
                OrdersFragmentDirections.actionOrdersFragmentToOrderDetailsFragment(order.id)
            findNavController().navigate(action)
        }
        binding.rvOrders.layoutManager = LinearLayoutManager(requireContext())
        binding.rvOrders.adapter = adapter
    }

    private fun setupToggle() = with(binding) {
        toggleStatus.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (!isChecked) return@addOnButtonCheckedListener
            val status = when (checkedId) {
                btnPending.id -> OrderStatus.PENDING
                btnDelivered.id -> OrderStatus.DELIVERED
                else -> OrderStatus.CANCELLED
            }
            viewModel.setStatus(status)
        }
        toggleStatus.check(btnPending.id)
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.ordersByStatus.collect { list ->
                    adapter.submitList(list)
                    binding.rvOrders.isVisible = list.isNotEmpty()
                }
            }
        }
    }
}
