package screen.address

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.sababukia_tbc.R
import com.example.sababukia_tbc.databinding.ItemAddressBinding
import model.Address
import model.AddressType

class AddressAdapter(
    private val onSelected: (Address) -> Unit,
    private val onEdit: (Address) -> Unit,
    private val onDelete: (Address) -> Unit
) : ListAdapter<Address, AddressAdapter.AddressViewHolder>(Diff) {

    private var selectedId: String? = null

    fun setSelected(id: String?) {
        if (selectedId == id) return
        val previous = selectedId
        selectedId = id
        val oldPos = previous?.let { prevId -> currentList.indexOfFirst { it.id == prevId } } ?: -1
        val newPos = id?.let { newId -> currentList.indexOfFirst { it.id == newId } } ?: -1
        if (oldPos != -1) notifyItemChanged(oldPos)
        if (newPos != -1) notifyItemChanged(newPos)
    }

    fun clearSelection() {
        if (selectedId == null) return
        val oldPos = currentList.indexOfFirst { it.id == selectedId }
        selectedId = null
        if (oldPos != -1) notifyItemChanged(oldPos)
    }

    fun getSelected(): String? = selectedId

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressViewHolder {
        val binding = ItemAddressBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AddressViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AddressViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class AddressViewHolder(private val binding: ItemAddressBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Address) = with(binding) {
            titleView.text = item.title
            addressLineView.text = item.addressLine

            val iconRes = when (item.type) {
                AddressType.HOUSE -> R.drawable.ic_home
                AddressType.APARTMENT -> R.drawable.ic_apartment
            }
            iconView.setImageResource(iconRes)
            iconView.visibility = View.VISIBLE

            radioButton.isChecked = item.id == selectedId

            root.setOnClickListener {
                setSelected(item.id)
                onSelected(item)
            }
            radioButton.setOnClickListener {
                setSelected(item.id)
                onSelected(item)
            }
            editView.setOnClickListener {
                onEdit(item)
            }
            root.setOnLongClickListener {
                onDelete(item)
                true
            }
        }
    }

    private object Diff : DiffUtil.ItemCallback<Address>() {
        override fun areItemsTheSame(oldItem: Address, newItem: Address): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Address, newItem: Address): Boolean =
            oldItem == newItem
    }
}
