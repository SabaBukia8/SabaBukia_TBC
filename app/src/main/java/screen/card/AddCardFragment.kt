package screen.card

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import basics.BaseFragment
import com.example.sababukia_tbc.databinding.FragmentAddCardBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import model.CardType
import util.CardFormatters

class AddCardFragment : BaseFragment<FragmentAddCardBinding>(FragmentAddCardBinding::inflate) {

    private val vm: CardViewModel by viewModels { VmFactory(requireActivity().application) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            btnBack.setOnClickListener {
                findNavController().navigateUp()
            }

            etName.addTextChangedListener { vm.updateName(it?.toString().orEmpty()) }

            etNumber.addTextChangedListener { vm.updateNumber(it?.toString().orEmpty()) }

            etExpiry.addTextChangedListener(object : TextWatcher {
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

            etCvv.addTextChangedListener { vm.updateCvv(it?.toString().orEmpty()) }

            rgType.check(rbMc.id)
            vm.updateType(CardType.MASTERCARD)

            rgType.setOnCheckedChangeListener { _, checkedId ->
                val type = when (checkedId) {
                    rbVisa.id -> CardType.VISA
                    else -> CardType.MASTERCARD
                }
                vm.updateType(type)
            }

            btnAdd.setOnClickListener { vm.submitCard() }


            viewLifecycleOwner.lifecycleScope.launch {
                vm.formState.collectLatest { s ->
                    updateCardPreview(s)
                    btnAdd.isEnabled = s.isValid

                    if (s.submitted) {
                        findNavController().navigateUp()
                    }
                }
            }
        }
    }

    private fun updateCardPreview(state: CardViewModel.FormState) {
        binding.preview.apply {
            tvNumber.text = CardFormatters.maskNumber(state.number)

            tvName.text = if (state.holderName.isBlank())
                getString(com.example.sababukia_tbc.R.string.placeholder_cardholder)
            else state.holderName.uppercase()

            val mm = state.expiryMonth.toIntOrNull() ?: 0
            val yy = state.expiryYear.toIntOrNull() ?: 0
            tvExpiry.text = CardFormatters.displayExpiry(mm, yy)

            when (state.type) {
                CardType.VISA -> {
                    logoVisa.visibility = View.VISIBLE
                    logoMc.visibility = View.GONE
                }
                CardType.MASTERCARD -> {
                    logoVisa.visibility = View.GONE
                    logoMc.visibility = View.VISIBLE
                }
            }
        }
    }
}