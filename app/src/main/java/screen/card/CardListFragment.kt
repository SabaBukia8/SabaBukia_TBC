package screen.card

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import basics.BaseFragment
import com.example.sababukia_tbc.databinding.FragmentCardListBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import model.Card

class CardListFragment : BaseFragment<FragmentCardListBinding>(FragmentCardListBinding::inflate) {

    private val vm: CardViewModel by viewModels { VmFactory(requireActivity().application) }
    private lateinit var adapter: CardPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parentFragmentManager.setFragmentResultListener(
            DeleteCardBottomSheet.REQUEST_KEY,
            this
        ) { _, bundle ->
            val id = bundle.getString(DeleteCardBottomSheet.RESULT_CARD_ID)
                ?: return@setFragmentResultListener
            vm.deleteCard(id)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = CardPagerAdapter(onLongPress = ::onCardLongPressed)
        with(binding) {
            viewPager.apply {
                adapter = this@CardListFragment.adapter
                orientation = ViewPager2.ORIENTATION_HORIZONTAL
                offscreenPageLimit = 1
                val pageMargin =
                    resources.getDimensionPixelOffset(com.example.sababukia_tbc.R.dimen.page_margin)
                setPageTransformer { page, position ->
                    page.translationX = -pageMargin * position
                    page.scaleY = 1 - (0.15f * kotlin.math.abs(position))
                    page.alpha = 1 - (0.3f * kotlin.math.abs(position))
                }
            }

            btnBack.setOnClickListener {
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }

            btnAddNew.setOnClickListener {
                vm.resetForm()
                val action = CardListFragmentDirections.actionCardListToAddCard()
                findNavController().navigate(action)
            }

            viewLifecycleOwner.lifecycleScope.launch {
                vm.cards.collectLatest { list ->
                    adapter.submitList(list)
                    tvEmpty.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
                    viewPager.visibility = if (list.isEmpty()) View.GONE else View.VISIBLE
                }
            }
        }
    }

    private fun onCardLongPressed(card: Card) {
        DeleteCardBottomSheet.new(card.id).show(parentFragmentManager, "delete")
    }
}