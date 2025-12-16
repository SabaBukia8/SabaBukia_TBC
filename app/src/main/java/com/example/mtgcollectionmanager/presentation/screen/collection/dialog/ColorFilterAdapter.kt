package com.example.mtgcollectionmanager.presentation.screen.collection.dialog

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mtgcollectionmanager.databinding.ItemColorFilterBinding
import com.example.mtgcollectionmanager.presentation.screen.collection.CollectionContract

data class ColorFilterItem(
    val code: String,
    val name: String,
    val state: CollectionContract.FilterState
)

class ColorFilterAdapter(
    colors: List<ColorFilterItem>,
    private val onColorStateChanged: (String, CollectionContract.FilterState) -> Unit
) : ListAdapter<ColorFilterItem, ColorFilterAdapter.ColorViewHolder>(ColorFilterDiffCallback()) {

    init {

        submitList(colors)
    }


    fun updateColorState(code: String, newState: CollectionContract.FilterState) {
        val newList = currentList.map { item ->
            if (item.code == code) {
                item.copy(state = newState)
            } else {
                item
            }
        }
        submitList(newList)
    }


    fun clearAllFilters() {
        val clearedList = currentList.map { item ->
            item.copy(state = CollectionContract.FilterState.NEUTRAL)
        }
        submitList(clearedList)
    }

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

    inner class ColorViewHolder(
        private val binding: ItemColorFilterBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(colorItem: ColorFilterItem) {
            with(binding) {
                tvColorName.text = colorItem.name
                updateStateIcon(colorItem.state)

                root.setOnClickListener {

                    val newState = when (colorItem.state) {
                        CollectionContract.FilterState.NEUTRAL -> CollectionContract.FilterState.INCLUDE
                        CollectionContract.FilterState.INCLUDE -> CollectionContract.FilterState.EXCLUDE
                        CollectionContract.FilterState.EXCLUDE -> CollectionContract.FilterState.NEUTRAL
                    }


                    updateColorState(colorItem.code, newState)


                    onColorStateChanged(colorItem.code, newState)
                }
            }
        }

        private fun updateStateIcon(state: CollectionContract.FilterState) {
            with(binding.ivFilterState) {
                when (state) {
                    CollectionContract.FilterState.NEUTRAL -> {
                        setImageDrawable(null)
                    }

                    CollectionContract.FilterState.INCLUDE -> {
                        setImageResource(com.example.mtgcollectionmanager.R.drawable.ic_check_mark)
                        clearColorFilter()
                    }

                    CollectionContract.FilterState.EXCLUDE -> {
                        setImageResource(com.example.mtgcollectionmanager.R.drawable.ic_redx)
                        clearColorFilter()
                    }
                }
            }
        }
    }

    class ColorFilterDiffCallback : DiffUtil.ItemCallback<ColorFilterItem>() {
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
