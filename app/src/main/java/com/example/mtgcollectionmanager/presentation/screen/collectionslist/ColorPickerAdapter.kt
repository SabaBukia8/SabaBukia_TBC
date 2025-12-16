package com.example.mtgcollectionmanager.presentation.screen.collectionslist

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mtgcollectionmanager.R
import com.example.mtgcollectionmanager.databinding.ItemColorPickerBinding
import com.google.android.material.card.MaterialCardView

data class ColorItem(
    val color: String,
    val isSelected: Boolean
)

class ColorPickerAdapter(
    private val onColorSelected: (String) -> Unit
) : ListAdapter<ColorItem, ColorPickerAdapter.ColorViewHolder>(ColorDiffCallback()) {

    // Define all available colors
    private val availableColors = listOf(
        "#F44336", // Red
        "#E91E63", // Pink
        "#9C27B0", // Purple
        "#673AB7", // Deep Purple
        "#3F51B5", // Indigo
        "#2196F3", // Blue
        "#03A9F4", // Light Blue
        "#00BCD4", // Cyan
        "#009688", // Teal
        "#4CAF50", // Green
        "#8BC34A", // Light Green
        "#CDDC39", // Lime
        "#FFEB3B", // Yellow
        "#FFC107", // Amber
        "#FF9800", // Orange
        "#FF5722", // Deep Orange
        "#795548", // Brown
        "#9E9E9E"  // Grey
    )
    
    // Initialize with the first color selected
    init {
        val initialList = availableColors.mapIndexed { index, color ->
            ColorItem(color, index == 0) // First item is selected by default
        }
        submitList(initialList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorViewHolder {
        val binding = ItemColorPickerBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, 
            false
        )
        return ColorViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ColorViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun getSelectedColor(): String {
        val selectedItem = currentList.find { it.isSelected }
        return selectedItem?.color ?: availableColors[0]
    }

    fun setSelectedColor(color: String) {
        // Create a new list with the selected color updated
        val newList = currentList.map { item ->
            item.copy(isSelected = item.color == color)
        }
        submitList(newList)
    }

    inner class ColorViewHolder(private val binding: ItemColorPickerBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(colorItem: ColorItem) {
            with(binding) {
                colorView.setBackgroundColor(Color.parseColor(colorItem.color))

                if (colorItem.isSelected) {
                    colorCard.strokeWidth = 6
                    colorCard.strokeColor = Color.BLACK
                } else {
                    colorCard.strokeWidth = 0
                }

                colorCard.setOnClickListener {
                    // Only update if this isn't already selected
                    if (!colorItem.isSelected) {
                        // Create a new list with this color selected
                        val newList = currentList.map { item ->
                            item.copy(isSelected = item.color == colorItem.color)
                        }
                        submitList(newList)
                        onColorSelected(colorItem.color)
                    }
                }
            }
        }
    }
    
    private class ColorDiffCallback : DiffUtil.ItemCallback<ColorItem>() {
        override fun areItemsTheSame(oldItem: ColorItem, newItem: ColorItem): Boolean {
            return oldItem.color == newItem.color
        }

        override fun areContentsTheSame(oldItem: ColorItem, newItem: ColorItem): Boolean {
            return oldItem == newItem
        }
    }
}