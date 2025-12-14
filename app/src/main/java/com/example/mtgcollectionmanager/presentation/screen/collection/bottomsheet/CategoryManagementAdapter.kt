package com.example.mtgcollectionmanager.presentation.screen.collection.bottomsheet

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mtgcollectionmanager.R
import com.example.mtgcollectionmanager.databinding.ItemCategoryBinding
import com.example.mtgcollectionmanager.domain.model.Category

class CategoryManagementAdapter(
    private val onEditClick: (Category) -> Unit,
    private val onDeleteClick: (Category) -> Unit
) : ListAdapter<Category, CategoryManagementAdapter.CategoryViewHolder>(CategoryDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = ItemCategoryBinding.inflate(
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
        private val binding: ItemCategoryBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(category: Category) {
            with(binding) {
                tvCategoryName.text = category.name
                tvCardCount.text = root.context.getString(R.string.card_count_format, category.cardCount)

                try {
                    vColorIndicator.setBackgroundColor(Color.parseColor(category.color))
                } catch (e: Exception) {
                    vColorIndicator.setBackgroundColor(Color.GRAY)
                }

                btnEdit.setOnClickListener {
                    onEditClick(category)
                }

                btnDelete.setOnClickListener {
                    onDeleteClick(category)
                }
            }
        }
    }

    private class CategoryDiffCallback : DiffUtil.ItemCallback<Category>() {
        override fun areItemsTheSame(oldItem: Category, newItem: Category): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Category, newItem: Category): Boolean {
            return oldItem == newItem
        }
    }
}
