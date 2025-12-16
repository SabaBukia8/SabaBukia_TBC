package com.example.mtgcollectionmanager.presentation.screen.collection.bottomsheet

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mtgcollectionmanager.R
import com.example.mtgcollectionmanager.databinding.ItemCollectionBinding
import com.example.mtgcollectionmanager.presentation.model.CollectionUi

class CollectionAdapter(
    private val onEditClick: (CollectionUi) -> Unit,
    private val onDeleteClick: (CollectionUi) -> Unit
) : ListAdapter<CollectionUi, CollectionAdapter.CollectionViewHolder>(CollectionDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CollectionViewHolder {
        val binding = ItemCollectionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CollectionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CollectionViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CollectionViewHolder(
        private val binding: ItemCollectionBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(collection: CollectionUi) {
            with(binding) {
                tvCollectionName.text = collection.name
                tvCollectionDescription.text = collection.description
                tvCardCount.text =
                    root.context.getString(R.string.card_count_format, collection.totalCards)
                tvTotalValue.text = collection.totalValue
                tvCreatedDate.text =
                    root.context.getString(R.string.created_date_format, collection.createdDate)

                btnMenu.setOnClickListener {
                    showPopupMenu(collection)
                }
            }
        }

        private fun showPopupMenu(collection: CollectionUi) {
            val popup = PopupMenu(binding.root.context, binding.btnMenu)
            popup.menuInflater.inflate(R.menu.menu_collection_item, popup.menu)
            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.action_edit -> {
                        onEditClick(collection)
                        true
                    }

                    R.id.action_delete -> {
                        onDeleteClick(collection)
                        true
                    }

                    else -> false
                }
            }
            popup.show()
        }
    }

    private class CollectionDiffCallback : DiffUtil.ItemCallback<CollectionUi>() {
        override fun areItemsTheSame(oldItem: CollectionUi, newItem: CollectionUi): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: CollectionUi, newItem: CollectionUi): Boolean {
            return oldItem == newItem
        }
    }
}
