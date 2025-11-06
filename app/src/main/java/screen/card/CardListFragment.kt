package screen.card

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

    private val vm: CardListViewModel by activityViewModels { VmFactory(requireActivity().application) }
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
        adapter = CardPagerAdapter(onLongPress = ::onCardLongPressed)
        binding.viewPager.adapter = adapter
        binding.viewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        binding.btnAddNew.setOnClickListener {
            val action = CardListFragmentDirections.actionCardListToAddCard()
            findNavController().navigate(action)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            vm.cards.collectLatest { list -> adapter.submitList(list) }
        }
    }

    private fun onCardLongPressed(card: Card) {
        DeleteCardBottomSheet.new(card.id).show(parentFragmentManager, "delete")
    }
}
