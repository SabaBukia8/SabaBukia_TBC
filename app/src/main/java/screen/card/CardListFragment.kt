package screen.card

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import basics.BaseFragment
import com.example.sababukia_tbc.databinding.FragmentCardListBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import model.Card

class CardListFragment : BaseFragment<FragmentCardListBinding>(FragmentCardListBinding::inflate) {

    private val vm: CardViewModel by activityViewModels { VmFactory(requireActivity().application) }
    private lateinit var adapter: CardPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parentFragmentManager.setFragmentResultListener(DeleteCardBottomSheet.REQUEST_KEY, this) { _, bundle ->
            val id = bundle.getString(DeleteCardBottomSheet.RESULT_CARD_ID) ?: return@setFragmentResultListener
            vm.deleteCard(id)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup adapter
        adapter = CardPagerAdapter(onLongPress = ::onCardLongPressed)
        binding.viewPager.adapter = adapter
        binding.viewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        // Set page transformer for better card carousel effect
        binding.viewPager.offscreenPageLimit = 1
        val pageMargin = resources.getDimensionPixelOffset(com.example.sababukia_tbc.R.dimen.page_margin)
        binding.viewPager.setPageTransformer { page, position ->
            page.translationX = -pageMargin * position
            page.scaleY = 1 - (0.15f * kotlin.math.abs(position))
            page.alpha = 1 - (0.3f * kotlin.math.abs(position))
        }

        // Back button
        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressed()
        }

        // Add new button
        binding.btnAddNew.setOnClickListener {
            vm.resetForm()
            val action = CardListFragmentDirections.actionCardListToAddCard()
            findNavController().navigate(action)
        }

        // Observe cards
        viewLifecycleOwner.lifecycleScope.launch {
            vm.cards.collectLatest { list ->
                adapter.submitList(list)
                binding.tvEmpty.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
                binding.viewPager.visibility = if (list.isEmpty()) View.GONE else View.VISIBLE
            }
        }
    }

    private fun onCardLongPressed(card: Card) {
        DeleteCardBottomSheet.new(card.id).show(parentFragmentManager, "delete")
    }
}