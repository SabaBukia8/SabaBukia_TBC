package screen.orders

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import basics.BaseFragment
import com.example.sababukia_tbc.databinding.FragmentOrdersListBinding
import data.OrderConstants
import kotlinx.coroutines.launch
import model.Order
import model.OrderStatus

class OrdersListFragment :
    BaseFragment<FragmentOrdersListBinding>(FragmentOrdersListBinding::inflate) {

    private val viewModel: OrdersViewModel by viewModels()
    private lateinit var adapter: OrdersAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecycler()
        observeState()
    }

    private fun setupRecycler() {
        adapter = OrdersAdapter(
            onDetailsClick = { /* no functionality for now */ },
            onReviewClick = { order -> showReviewSheet(order) },
            onBuyAgainClick = { /*no functionality for now*/ }
        )
        binding.rvOrders.layoutManager = LinearLayoutManager(requireContext())
        binding.rvOrders.adapter = adapter
    }

    private fun statusArg(): OrderStatus = OrderStatus.valueOf(
        requireArguments().getString(OrderConstants.ARG_STATUS)!!
    )

    private fun observeState() {
        val status = statusArg()
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.ordersFor(status).collect { list ->
                    adapter.submitList(list)
                    binding.rvOrders.isVisible = list.isNotEmpty()
                }
            }
        }
    }

    private fun showReviewSheet(order: Order) {
        val sheet = LeaveReviewBottomSheet.newInstance(order.id)
        sheet.onSubmit = { rating, text ->
            viewModel.submitReview(order.id, rating, text)
        }
        sheet.show(parentFragmentManager, OrderConstants.TAG_LEAVE_REVIEW)
    }

    companion object {
        fun newInstance(status: OrderStatus) = OrdersListFragment().apply {
            arguments = bundleOf(OrderConstants.ARG_STATUS to status.name)
        }
    }
}