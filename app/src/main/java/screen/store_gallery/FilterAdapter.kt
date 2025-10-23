package screen.store_gallery

import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.sababukia_tbc.databinding.FilterButtonItemBinding

class FilterAdapter(
    private val onButtonClick: (FilterButton) -> Unit
) : ListAdapter<FilterButton, FilterAdapter.UserViewHolder>(FilterDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding =
            FilterButtonItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class FilterDiffCallback : DiffUtil.ItemCallback<FilterButton>() {
        override fun areItemsTheSame(oldItem: FilterButton, newItem: FilterButton): Boolean {
            return oldItem.text == newItem.text
        }

        override fun areContentsTheSame(oldItem: FilterButton, newItem: FilterButton): Boolean {
            return oldItem == newItem
        }
    }

    inner class UserViewHolder(private val binding: FilterButtonItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(button: FilterButton) {
            setupButton(button)
        }

        private fun setupButton(button: FilterButton) = with(binding) {
            filterButton.text = button.text
            filterButton.isSelected = button.isSelected
            root.setOnClickListener {
                onButtonClick(button)
            }

            val layoutParams = filterButton.layoutParams
            if (button.text == "All") {
                layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT
            } else {
                layoutParams.width = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    125f,
                    root.context.resources.displayMetrics
                ).toInt()
            }
            filterButton.layoutParams = layoutParams
        }
    }
}
