package com.example.mtgcollectionmanager.presentation.screen.search.dialog

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mtgcollectionmanager.R
import com.example.mtgcollectionmanager.databinding.ItemColorFilterBinding
import com.example.mtgcollectionmanager.presentation.screen.search.model.ColorFilterState

data class ColorFilterItem(
    val code: String,
    val name: String,
    val state: ColorFilterState
)

class SearchColorFilterAdapter(
    private val onColorStateChanged: (String, ColorFilterState) -> Unit
) : ListAdapter<ColorFilterItem, SearchColorFilterAdapter.ColorViewHolder>(ColorFilterDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorViewHolder {
        val binding = ItemColorFilterBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ColorViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ColorViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun updateItemState(position: Int, newState: ColorFilterState) {
        val currentList = currentList.toMutableList()
        if (position >= 0 && position < currentList.size) {
            val oldItem = currentList[position]
            currentList[position] = oldItem.copy(state = newState)
            submitList(currentList)
        }
    }

    fun resetAllStates() {
        val resetList = currentList.map { it.copy(state = ColorFilterState.NEUTRAL) }
        submitList(resetList)
    }

    fun getColorStates(): Map<String, ColorFilterState> {
        return currentList.associate { it.code to it.state }
            .filter { it.value != ColorFilterState.NEUTRAL }
    }

    inner class ColorViewHolder(
        private val binding: ItemColorFilterBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(colorItem: ColorFilterItem) {
            with(binding) {
                tvColorName.text = colorItem.name
                updateStateIcon(colorItem.state)

                root.setOnClickListener {
                    val position = bindingAdapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val newState = when (colorItem.state) {
                            ColorFilterState.NEUTRAL -> ColorFilterState.INCLUDE
                            ColorFilterState.INCLUDE -> ColorFilterState.EXCLUDE
                            ColorFilterState.EXCLUDE -> ColorFilterState.NEUTRAL
                        }
                        updateItemState(position, newState)
                        onColorStateChanged(colorItem.code, newState)
                    }
                }
            }
        }

        private fun updateStateIcon(state: ColorFilterState) {
            with(binding.ivFilterState) {
                when (state) {
                    ColorFilterState.NEUTRAL -> {
                        setImageDrawable(null)
                    }

                    ColorFilterState.INCLUDE -> {
                        setImageResource(R.drawable.ic_check_mark)
                        clearColorFilter()
                    }

                    ColorFilterState.EXCLUDE -> {
                        setImageResource(R.drawable.ic_redx)
                        clearColorFilter()
                    }
                }
            }
        }
    }

    private class ColorFilterDiffCallback : DiffUtil.ItemCallback<ColorFilterItem>() {
        override fun areItemsTheSame(oldItem: ColorFilterItem, newItem: ColorFilterItem): Boolean {
            return oldItem.code == newItem.code
        }

        override fun areContentsTheSame(
            oldItem: ColorFilterItem,
            newItem: ColorFilterItem
        ): Boolean {
            return oldItem == newItem
        }
    }
}