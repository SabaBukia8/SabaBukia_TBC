package screen.card

import android.os.Bundle
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import basics.BaseFragment
import com.example.sababukia_tbc.databinding.FragmentAddCardBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import model.CardType
import util.CardFormatters

class AddCardFragment : BaseFragment<FragmentAddCardBinding>(FragmentAddCardBinding::inflate) {

    private val vm: AddCardViewModel by activityViewModels { VmFactory(requireActivity().application) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Form listeners
        binding.etName.addTextChangedListener { vm.updateName(it?.toString().orEmpty()) }
        binding.etNumber.addTextChangedListener { vm.updateNumber(it?.toString().orEmpty()) }
        binding.etMonth.addTextChangedListener { vm.updateMonth(it?.toString().orEmpty()) }
        binding.etCvv.addTextChangedListener { vm.updateCvv(it?.toString().orEmpty()) }

        binding.rgType.setOnCheckedChangeListener { _, checkedId ->
            vm.updateType(if (checkedId == binding.rbVisa.id) CardType.VISA else CardType.MASTERCARD)
        }

        binding.btnAdd.setOnClickListener { vm.submit() }

        // Observe state to update preview and navigation
        viewLifecycleOwner.lifecycleScope.launch {
            vm.state.collectLatest { s ->
                // Preview card
                val cardBinding = com.example.sababukia_tbc.databinding.ItemCardBinding.bind(binding.preview.root)

                cardBinding.tvNumber.text = CardFormatters.maskNumber(s.number)
                cardBinding.tvName.text = if (s.holderName.isBlank()) getString(com.example.sababukia_tbc.R.string.placeholder_cardholder) else s.holderName
                val mm = s.expiryMonth.toIntOrNull() ?: 0
                val yy = s.expiryYear.toIntOrNull() ?: 0
                cardBinding.tvExpiry.text = CardFormatters.displayExpiry(mm, yy)
                cardBinding.logoVisa.alpha = if (s.type == CardType.VISA) 1f else 0.2f
                cardBinding.logoMc.alpha = if (s.type == CardType.MASTERCARD) 1f else 0.2f

                binding.btnAdd.isEnabled = s.isValid

                if (s.submitted) findNavController().navigateUp()
            }
        }
    }
}
