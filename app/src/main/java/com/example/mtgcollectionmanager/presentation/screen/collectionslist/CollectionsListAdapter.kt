package com.example.mtgcollectionmanager.presentation.screen.collectionslist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mtgcollectionmanager.R
import com.example.mtgcollectionmanager.presentation.model.CollectionUi

class CollectionsListAdapter(
    private val onCollectionClick: (Long) -> Unit,
    private val onEditClick: (Long) -> Unit,
    private val onDeleteClick: (Long) -> Unit
) : ListAdapter<CollectionUi, CollectionsListAdapter.CollectionViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CollectionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_collection, parent, false)
        return CollectionViewHolder(view)
    }

    override fun onBindViewHolder(holder: CollectionViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CollectionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvCollectionName: TextView = itemView.findViewById(R.id.tvCollectionName)
        private val tvCollectionDescription: TextView = itemView.findViewById(R.id.tvCollectionDescription)
        private val tvCardCount: TextView = itemView.findViewById(R.id.tvCardCount)
        private val tvTotalValue: TextView = itemView.findViewById(R.id.tvTotalValue)
        private val tvCreatedDate: TextView = itemView.findViewById(R.id.tvCreatedDate)
        private val btnMenu: ImageButton = itemView.findViewById(R.id.btnMenu)

        fun bind(collection: CollectionUi) {
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

            itemView.setOnClickListener {
                onCollectionClick(collection.id)
            }

            btnMenu.setOnClickListener {
                showPopupMenu(it, collection)
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
