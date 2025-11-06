package screen.card

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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

    private val vm: CardViewModel by activityViewModels { VmFactory(requireActivity().application) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Back button
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        // Form listeners
        binding.etName.addTextChangedListener { vm.updateName(it?.toString().orEmpty()) }

        binding.etNumber.addTextChangedListener { vm.updateNumber(it?.toString().orEmpty()) }

        // Handle MM/YY format for expiry
        binding.etExpiry.addTextChangedListener(object : TextWatcher {
            private var isFormatting = false

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (isFormatting) return
                isFormatting = true

                val input = s?.toString()?.filter { it.isDigit() } ?: ""

                when {
                    input.length >= 2 -> {
                        val month = input.substring(0, 2)
                        val year = if (input.length > 2) input.substring(2).take(2) else ""
                        vm.updateMonth(month)
                        vm.updateYear(if (year.isNotEmpty()) "20$year" else "")

                        val formatted = if (year.isNotEmpty()) "$month/$year" else month
                        if (s.toString() != formatted) {
                            s?.replace(0, s.length, formatted)
                        }
                    }
                    else -> {
                        vm.updateMonth(input)
                        vm.updateYear("")
                    }
                }

                isFormatting = false
            }
        })

        binding.etCvv.addTextChangedListener { vm.updateCvv(it?.toString().orEmpty()) }

        // Card type selection
        binding.rgType.check(binding.rbMc.id)
        vm.updateType(CardType.MASTERCARD)

        binding.rgType.setOnCheckedChangeListener { _, checkedId ->
            val type = when (checkedId) {
                binding.rbVisa.id -> CardType.VISA
                else -> CardType.MASTERCARD
            }
            vm.updateType(type)
        }

        binding.btnAdd.setOnClickListener { vm.submitCard() }

        // Observe state to update preview and navigation
        viewLifecycleOwner.lifecycleScope.launch {
            vm.formState.collectLatest { s ->
                updateCardPreview(s)
                binding.btnAdd.isEnabled = s.isValid

                if (s.submitted) {
                    findNavController().navigateUp()
                }
            }
        }
    }

    private fun updateCardPreview(state: CardViewModel.FormState) {
        val cardBinding = com.example.sababukia_tbc.databinding.ItemCardBinding.bind(binding.preview.root)

        // Update card number
        cardBinding.tvNumber.text = CardFormatters.maskNumber(state.number)

        // Update cardholder name
        cardBinding.tvName.text = if (state.holderName.isBlank())
            getString(com.example.sababukia_tbc.R.string.placeholder_cardholder)
        else state.holderName.uppercase()

        // Update expiry
        val mm = state.expiryMonth.toIntOrNull() ?: 0
        val yy = state.expiryYear.toIntOrNull() ?: 0
        cardBinding.tvExpiry.text = CardFormatters.displayExpiry(mm, yy)

        // Update card type visibility
        when (state.type) {
            CardType.VISA -> {
                cardBinding.logoVisa.visibility = View.VISIBLE
                cardBinding.logoMc.visibility = View.GONE
                cardBinding.mcCircle1.visibility = View.GONE
                cardBinding.mcCircle2.visibility = View.GONE
            }
            CardType.MASTERCARD -> {
                cardBinding.logoVisa.visibility = View.GONE
                cardBinding.logoMc.visibility = View.VISIBLE
                cardBinding.mcCircle1.visibility = View.VISIBLE
                cardBinding.mcCircle2.visibility = View.VISIBLE
            }
        }
    }
}