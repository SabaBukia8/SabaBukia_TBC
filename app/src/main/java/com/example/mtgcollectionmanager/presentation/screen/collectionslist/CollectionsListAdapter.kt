package com.example.mtgcollectionmanager.presentation.screen.collectionslist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mtgcollectionmanager.R
import com.example.mtgcollectionmanager.databinding.ItemCollectionBinding
import com.example.mtgcollectionmanager.presentation.model.CollectionUi

class CollectionsListAdapter(
    private val onCollectionClick: (Long) -> Unit,
    private val onEditClick: (Long) -> Unit,
    private val onDeleteClick: (Long) -> Unit
) : ListAdapter<CollectionUi, CollectionsListAdapter.CollectionViewHolder>(DiffCallback()) {

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

    inner class CollectionViewHolder(private val binding: ItemCollectionBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(collection: CollectionUi) {
            with(binding) {
                tvCollectionName.text = collection.name
                tvCollectionDescription.text = collection.description
                tvCardCount.text = "${collection.totalCards} cards"
                tvTotalValue.text = collection.totalValue
                tvCreatedDate.text = "Created: ${collection.createdDate}"

                // Show/hide description based on whether it's empty
                tvCollectionDescription.visibility = if (collection.description.isEmpty()) {
                    View.GONE
                } else {
                    View.VISIBLE
                }

                root.setOnClickListener {
                    onCollectionClick(collection.id)
                }

                btnMenu.setOnClickListener {
                    showPopupMenu(it, collection)
                }
            }
        }

        private fun showPopupMenu(view: View, collection: CollectionUi) {
            val popup = PopupMenu(view.context, view)
            popup.menuInflater.inflate(R.menu.collection_item_menu, popup.menu)
            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.action_edit -> {
                        onEditClick(collection.id)
                        true
                    }
                    R.id.action_delete -> {
                        onDeleteClick(collection.id)
                        true
                    }
                    else -> false
                }
            }
            popup.show()
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<CollectionUi>() {
        override fun areItemsTheSame(oldItem: CollectionUi, newItem: CollectionUi): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: CollectionUi, newItem: CollectionUi): Boolean {
            return oldItem == newItem
        }
    }
}