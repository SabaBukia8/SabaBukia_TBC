package screen.address

import androidx.navigation.fragment.findNavController
import basics.BaseFragment
import com.example.sababukia_tbc.R
import com.example.sababukia_tbc.databinding.FragmentAddressFormBinding
import model.Address
import model.AddressType

class AddAddressFragment :
    BaseFragment<FragmentAddressFormBinding>(FragmentAddressFormBinding::inflate) {

    override fun bind() {
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
            val newAddress = Address(title = title, addressLine = addressLine, type = selectedType)
            parentFragmentManager.setFragmentResult(
                DeliveryAddressesFragment.RESULT_ADD_KEY,
                android.os.Bundle().apply {
                    putParcelable(DeliveryAddressesFragment.RESULT_ADDRESS_KEY, newAddress)
                }
            )
            findNavController().navigateUp()
        }
    }
}