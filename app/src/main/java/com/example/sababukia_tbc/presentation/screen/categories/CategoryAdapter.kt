package com.example.sababukia_tbc.presentation.screen.categories

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.sababukia_tbc.R
import com.example.sababukia_tbc.databinding.ItemCategoryBinding
import com.example.sababukia_tbc.presentation.model.CategoryModel

class CategoryAdapter : ListAdapter<CategoryModel, CategoryAdapter.ViewHolder>(DiffCallback()) {

    companion object {
        private const val DEPTH_OFFSET = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCategoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(
        private val binding: ItemCategoryBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        private val depthIndicators = with(binding) {
            listOf(ivDepth0, ivDepth1, ivDepth2, ivDepth3, ivDepth4)
        }

        private val indentPerLevel = itemView.context.resources.getDimensionPixelSize(R.dimen.indent_per_level)

        fun bind(category: CategoryModel) {
            binding.tvCategoryName.text = category.name

            binding.spaceIndent.updateLayoutParams {
                width = category.depth * indentPerLevel
            }

            depthIndicators.forEachIndexed { index, imageView ->
                imageView.isVisible = category.depth >= DEPTH_OFFSET + index
            }
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<CategoryModel>() {
        override fun areItemsTheSame(oldItem: CategoryModel, newItem: CategoryModel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: CategoryModel, newItem: CategoryModel): Boolean {
            return oldItem == newItem
        }
    }
}