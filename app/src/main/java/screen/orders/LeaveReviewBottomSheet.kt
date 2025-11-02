package screen.orders

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.sababukia_tbc.R
import com.example.sababukia_tbc.databinding.BottomSheetLeaveReviewBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.transition.platform.MaterialSharedAxis
import data.OrderConstants
import kotlinx.coroutines.launch
import model.OrderStatus
import java.util.Locale

class LeaveReviewBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomSheetLeaveReviewBinding? = null
    private val binding get() = _binding!!

    private val viewModel: OrdersViewModel by viewModels()

    var onSubmit: ((rating: Int, text: String) -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Y, true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Y, false)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetLeaveReviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnCancel.setOnClickListener { dismiss() }
        binding.btnSubmit.isEnabled = false
        binding.ratingBar.setOnRatingBarChangeListener { _, rating, _ ->
            binding.btnSubmit.isEnabled = rating > 0f
        }
        binding.btnSubmit.setOnClickListener {
            val rating = binding.ratingBar.rating.toInt().coerceIn(1, 5)
            val text = binding.etReview.text?.toString()?.trim().orEmpty()
            onSubmit?.invoke(rating, text)
            dismiss()
        }

        val orderId = requireArguments().getLong(OrderConstants.ARG_ORDER_ID)
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.getOrder(orderId).collect { order ->
                    if (order == null) return@collect
                    with(binding) {
                        ivProduct.setImageResource(order.imageResId)
                        tvProductTitle.text = order.title

                        vColorDot.backgroundTintList = ColorStateList.valueOf(order.colorArgb)
                        tvColor.text = order.colorName
                        tvQuantity.text = getString(R.string.qty_equals, order.quantity)

                        tvStatus.text = when (order.status) {
                            OrderStatus.ACTIVE -> getString(R.string.status_active)
                            OrderStatus.COMPLETED -> getString(R.string.status_completed)
                        }
                        val statusColorRes = when (order.status) {
                            OrderStatus.ACTIVE -> R.color.orange
                            OrderStatus.COMPLETED -> R.color.green
                        }
                        tvStatus.backgroundTintList = ColorStateList.valueOf(
                            ContextCompat.getColor(requireContext(), statusColorRes)
                        )

                        tvPrice.text = formatPrice(order.subtotalCents)

                        binding.ratingBar.rating = (order.rating ?: 0).toFloat()
                        binding.etReview.setText(order.reviewText.orEmpty())
                    }
                }
            }
        }
    }

    private fun formatPrice(cents: Int): String {
        val dollars = cents.toDouble() / OrderConstants.CENTS_PER_DOLLAR
        return String.format(Locale.US, "$%.${OrderConstants.PRICE_DECIMAL_PLACES}f", dollars)
    }

    override fun onDestroyView() {
        onSubmit = null  //
        _binding = null
        super.onDestroyView()
    }

    companion object {
        fun newInstance(orderId: Long) = LeaveReviewBottomSheet().apply {
            arguments = Bundle().apply { putLong(OrderConstants.ARG_ORDER_ID, orderId) }
        }
    }
}