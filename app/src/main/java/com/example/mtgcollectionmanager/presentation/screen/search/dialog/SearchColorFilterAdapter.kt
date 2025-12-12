package com.example.mtgcollectionmanager.presentation.screen.search.dialog

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mtgcollectionmanager.R
import com.example.mtgcollectionmanager.databinding.ItemColorFilterBinding
import com.example.mtgcollectionmanager.presentation.screen.search.model.ColorFilterState

data class ColorFilterItem(
    val code: String,
    val name: String,
    var state: ColorFilterState
)

class SearchColorFilterAdapter(
    val colorItems: MutableList<ColorFilterItem>,
    private val onColorStateChanged: (String, ColorFilterState) -> Unit
) : RecyclerView.Adapter<SearchColorFilterAdapter.ColorViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorViewHolder {
        val binding = ItemColorFilterBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ColorViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ColorViewHolder, position: Int) {
        holder.bind(colorItems[position])
    }

    override fun getItemCount(): Int = colorItems.size

    inner class ColorViewHolder(
        private val binding: ItemColorFilterBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(colorItem: ColorFilterItem) {
            with(binding) {
                tvColorName.text = colorItem.name
                updateStateIcon(colorItem.state)

                root.setOnClickListener {
                    // Cycle through states: NEUTRAL -> INCLUDE -> EXCLUDE -> NEUTRAL
                    val newState = when (colorItem.state) {
                        ColorFilterState.NEUTRAL -> ColorFilterState.INCLUDE
                        ColorFilterState.INCLUDE -> ColorFilterState.EXCLUDE
                        ColorFilterState.EXCLUDE -> ColorFilterState.NEUTRAL
                    }
                    colorItem.state = newState
                    updateStateIcon(newState)
                    onColorStateChanged(colorItem.code, newState)
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
}
