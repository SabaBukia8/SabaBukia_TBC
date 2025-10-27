package screen.address

import android.app.AlertDialog
import android.os.Bundle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import basics.BaseFragment
import com.example.sababukia_tbc.databinding.FragmentDeliveryAddressesBinding
import model.Address

class DeliveryAddressesFragment :
    BaseFragment<FragmentDeliveryAddressesBinding>(FragmentDeliveryAddressesBinding::inflate) {

    private lateinit var adapter: AddressAdapter
    private val addresses = mutableListOf<Address>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parentFragmentManager.setFragmentResultListener(RESULT_ADD_KEY, this) { _, result ->
            val address = androidx.core.os.BundleCompat.getParcelable(
                result,
                RESULT_ADDRESS_KEY,
                Address::class.java
            ) ?: return@setFragmentResultListener
            addresses.add(address)
            adapter.submitList(addresses.toList())
        }
        parentFragmentManager.setFragmentResultListener(RESULT_EDIT_KEY, this) { _, result ->
            val address = androidx.core.os.BundleCompat.getParcelable(
                result,
                RESULT_ADDRESS_KEY,
                Address::class.java
            ) ?: return@setFragmentResultListener
            val index = addresses.indexOfFirst { it.id == address.id }
            if (index != -1) {
                addresses[index] = address
                adapter.submitList(addresses.toList())
            }
        }
    }

    override fun bind() = with(binding) {
        setupRecycler()
        // Ensure current data (if any) is reflected
        if (this@DeliveryAddressesFragment::adapter.isInitialized) {
            adapter.submitList(addresses.toList())
        }
    }

    override fun listeners() = with(binding) {
        btnAdd
            .setOnClickListener {
                findNavController().navigate(DeliveryAddressesFragmentDirections.actionDeliveryAddressesFragmentToAddAddressFragment())
            }
        btnEdit.setOnClickListener {
            val selectedId = adapter.getSelected() ?: return@setOnClickListener
            val item = addresses.firstOrNull { it.id == selectedId } ?: return@setOnClickListener
            val action =
                DeliveryAddressesFragmentDirections.actionDeliveryAddressesFragmentToEditAddressFragment(
                    item
                )
            findNavController().navigate(action)
        }
    }

    private fun setupRecycler() = with(binding) {
        adapter = AddressAdapter(
            onSelected = { btnEdit.isEnabled = true },
            onEdit = { address ->
                val action =
                    DeliveryAddressesFragmentDirections.actionDeliveryAddressesFragmentToEditAddressFragment(
                        address
                    )
                findNavController().navigate(action)
            },
            onDelete = { address -> confirmDelete(address) }
        )
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
    }

    private fun confirmDelete(address: Address) {
        AlertDialog.Builder(requireContext())
            .setMessage("Delete this address?")
            .setPositiveButton("Delete") { _, _ ->
                val index = addresses.indexOfFirst { it.id == address.id }
                if (index != -1) {
                    addresses.removeAt(index)
                    adapter.submitList(addresses.toList())
                    if (adapter.getSelected() == address.id) {
                        adapter.clearSelection()
                        binding.btnEdit.isEnabled = false
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    companion object {
        const val RESULT_ADD_KEY = "result_add_key"
        const val RESULT_EDIT_KEY = "result_edit_key"
        const val RESULT_ADDRESS_KEY = "result_address_key"
    }
}
