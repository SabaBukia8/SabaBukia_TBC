package com.example.mtgcollectionmanager.presentation.screen.collection.dialog

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.mtgcollectionmanager.R
import com.example.mtgcollectionmanager.databinding.ItemColorFilterBinding
import com.example.mtgcollectionmanager.presentation.screen.collection.CollectionContract

data class ColorFilterItem(
    val code: String,
    val name: String,
    var state: CollectionContract.FilterState
)

class ColorFilterAdapter(
    private val colors: List<ColorFilterItem>,
    private val onColorStateChanged: (String, CollectionContract.FilterState) -> Unit
) : RecyclerView.Adapter<ColorFilterAdapter.ColorViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorViewHolder {
        val binding = ItemColorFilterBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ColorViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ColorViewHolder, position: Int) {
        holder.bind(colors[position])
    }

    override fun getItemCount(): Int = colors.size

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
                        CollectionContract.FilterState.NEUTRAL -> CollectionContract.FilterState.INCLUDE
                        CollectionContract.FilterState.INCLUDE -> CollectionContract.FilterState.EXCLUDE
                        CollectionContract.FilterState.EXCLUDE -> CollectionContract.FilterState.NEUTRAL
                    }
                    colorItem.state = newState
                    updateStateIcon(newState)
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
}
