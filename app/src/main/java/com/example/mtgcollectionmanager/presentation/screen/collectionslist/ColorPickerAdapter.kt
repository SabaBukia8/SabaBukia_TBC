package com.example.mtgcollectionmanager.presentation.screen.collectionslist

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.mtgcollectionmanager.R
import com.google.android.material.card.MaterialCardView

class ColorPickerAdapter(
    private val onColorSelected: (String) -> Unit
) : RecyclerView.Adapter<ColorPickerAdapter.ColorViewHolder>() {

    private val colors = listOf(
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

    private var selectedColor: String = colors[0]

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_color_picker, parent, false)
        return ColorViewHolder(view)
    }

    override fun onBindViewHolder(holder: ColorViewHolder, position: Int) {
        holder.bind(colors[position])
    }

    override fun getItemCount() = colors.size

    fun getSelectedColor() = selectedColor

    fun setSelectedColor(color: String) {
        selectedColor = color
        notifyDataSetChanged()
    }

    inner class ColorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val colorCard: MaterialCardView = itemView.findViewById(R.id.colorCard)
        private val colorView: View = itemView.findViewById(R.id.colorView)

        fun bind(color: String) {
            colorView.setBackgroundColor(Color.parseColor(color))

            if (color == selectedColor) {
                colorCard.strokeWidth = 6
                colorCard.strokeColor = Color.BLACK
            } else {
                colorCard.strokeWidth = 0
            }

            colorCard.setOnClickListener {
                selectedColor = color
                onColorSelected(color)
                notifyDataSetChanged()
            }
        }
    }
}
