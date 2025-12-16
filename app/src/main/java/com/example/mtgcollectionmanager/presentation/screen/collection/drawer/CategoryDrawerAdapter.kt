package com.example.mtgcollectionmanager.presentation.screen.collection.drawer

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mtgcollectionmanager.R
import com.example.mtgcollectionmanager.databinding.ItemCategoryDrawerBinding

class CategoryDrawerAdapter(
    private val onItemClick: (Long?) -> Unit
) : ListAdapter<CategoryDrawerItem, CategoryDrawerAdapter.CategoryViewHolder>(CategoryDiffCallback()) {

    private var selectedCategoryId: Long? = null

    fun setSelectedCategory(categoryId: Long?) {
        val oldCategoryId = selectedCategoryId
        selectedCategoryId = categoryId
        
        // Optimize updates by only redrawing affected items
        if (oldCategoryId != categoryId) {
            // Find and update the previously selected category
            if (oldCategoryId != null) {
                val oldPosition = findPositionById(oldCategoryId)
                if (oldPosition != -1) {
                    notifyItemChanged(oldPosition)
                }
            } else {
                // The "All Cards" item was previously selected (position 0)
                notifyItemChanged(0)
            }
            
            // Find and update the newly selected category
            if (categoryId != null) {
                val newPosition = findPositionById(categoryId)
                if (newPosition != -1) {
                    notifyItemChanged(newPosition)
                }
            } else {
                // The "All Cards" item is now selected (position 0)
                notifyItemChanged(0)
            }
        }
    }
    
    private fun findPositionById(categoryId: Long): Int {
        for (position in 0 until itemCount) {
            val item = getItem(position)
            if (item is CategoryDrawerItem.Category && item.id == categoryId) {
                return position
            } else if (item is CategoryDrawerItem.Uncategorized && categoryId == -1L) {
                return position
            }
        }
        return -1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = ItemCategoryDrawerBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CategoryViewHolder(
        private val binding: ItemCategoryDrawerBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CategoryDrawerItem) {
            with(binding) {
                when (item) {
                    is CategoryDrawerItem.AllCards -> {
                        tvCategoryName.text = root.context.getString(R.string.category_all)
                        tvCardCount.isVisible = false
                        vColorIndicator.isVisible = false
                        ivIcon.isVisible = true
                        ivIcon.setImageResource(android.R.drawable.ic_menu_view)

                        root.setBackgroundColor(
                            if (selectedCategoryId == null) {
                                root.context.getColor(android.R.color.holo_blue_light)
                            } else {
                                Color.TRANSPARENT
                            }
                        )

                        root.setOnClickListener {
                            onItemClick(null)
                        }
                    }
                    is CategoryDrawerItem.Uncategorized -> {
                        tvCategoryName.text = root.context.getString(R.string.category_uncategorized)
                        tvCardCount.isVisible = false
                        vColorIndicator.isVisible = false
                        ivIcon.isVisible = true
                        ivIcon.setImageResource(android.R.drawable.ic_menu_help)

                        root.setBackgroundColor(
                            if (selectedCategoryId == -1L) {
                                root.context.getColor(android.R.color.holo_blue_light)
                            } else {
                                Color.TRANSPARENT
                            }
                        )

                        root.setOnClickListener {
                            onItemClick(-1L) // Use -1 to represent uncategorized
                        }
                    }
                    is CategoryDrawerItem.Category -> {
                        tvCategoryName.text = item.name
                        tvCardCount.isVisible = true
                        tvCardCount.text = item.cardCount.toString()
                        vColorIndicator.isVisible = true
                        ivIcon.isVisible = false

                        try {
                            vColorIndicator.setBackgroundColor(Color.parseColor(item.color))
                        } catch (e: Exception) {
                            vColorIndicator.setBackgroundColor(Color.GRAY)
                        }

                        root.setBackgroundColor(
                            if (selectedCategoryId == item.id) {
                                root.context.getColor(android.R.color.holo_blue_light)
                            } else {
                                Color.TRANSPARENT
                            }
                        )

                        root.setOnClickListener {
                            onItemClick(item.id)
                        }
                    }
                }
            }
        }
    }

    private class CategoryDiffCallback : DiffUtil.ItemCallback<CategoryDrawerItem>() {
        override fun areItemsTheSame(oldItem: CategoryDrawerItem, newItem: CategoryDrawerItem): Boolean {
            return when {
                oldItem is CategoryDrawerItem.AllCards && newItem is CategoryDrawerItem.AllCards -> true
                oldItem is CategoryDrawerItem.Uncategorized && newItem is CategoryDrawerItem.Uncategorized -> true
                oldItem is CategoryDrawerItem.Category && newItem is CategoryDrawerItem.Category ->
                    oldItem.id == newItem.id
                else -> false
            }
        }

        override fun areContentsTheSame(oldItem: CategoryDrawerItem, newItem: CategoryDrawerItem): Boolean {
            return oldItem == newItem
        }
    }
}
