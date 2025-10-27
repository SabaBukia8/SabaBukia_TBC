package screen.address

import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import basics.BaseFragment
import com.example.sababukia_tbc.R
import com.example.sababukia_tbc.databinding.FragmentAddressFormBinding
import model.AddressType

class EditAddressFragment :
    BaseFragment<FragmentAddressFormBinding>(FragmentAddressFormBinding::inflate) {

    private val args: EditAddressFragmentArgs by navArgs()

    override fun bind() {
        val existing = args.address
        binding.etTitle.setText(existing.title)
        binding.etAddress.setText(existing.addressLine)

        // Preselect current type
        when (existing.type) {
            AddressType.HOUSE -> binding.radioHouse.isChecked = true
            AddressType.APARTMENT -> binding.radioApartment.isChecked = true
        }

        binding.btnSave.text = getString(R.string.save_changes)

        binding.btnSave.setOnClickListener {
            val title = binding.etTitle.text?.toString()?.trim().orEmpty()
            val addressLine = binding.etAddress.text?.toString()?.trim().orEmpty()
            if (title.isEmpty() || addressLine.isEmpty()) {
                binding.etTitle.error = if (title.isEmpty()) getString(R.string.required) else null
                binding.etAddress.error =
                    if (addressLine.isEmpty()) getString(R.string.required) else null
                return@setOnClickListener
            }
            val selectedType =
                if (binding.radioApartment.isChecked) AddressType.APARTMENT else AddressType.HOUSE
            val updated =
                existing.copy(title = title, addressLine = addressLine, type = selectedType)
            parentFragmentManager.setFragmentResult(
                DeliveryAddressesFragment.RESULT_EDIT_KEY,
                android.os.Bundle().apply {
                    putParcelable(DeliveryAddressesFragment.RESULT_ADDRESS_KEY, updated)
                }
            )
            findNavController().navigateUp()
        }
    }
}