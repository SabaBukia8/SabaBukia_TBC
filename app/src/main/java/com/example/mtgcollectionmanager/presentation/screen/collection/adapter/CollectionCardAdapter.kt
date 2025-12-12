package com.example.mtgcollectionmanager.presentation.screen.collection.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mtgcollectionmanager.databinding.ItemCollectionCardBinding
import com.example.mtgcollectionmanager.presentation.common.loadImage
import com.example.mtgcollectionmanager.presentation.model.CollectionCardUiModel

class CollectionCardAdapter(
    private val onCardClick: (String) -> Unit
) : ListAdapter<CollectionCardUiModel, CollectionCardAdapter.CollectionCardViewHolder>(CollectionCardDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CollectionCardViewHolder {
        val binding = ItemCollectionCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CollectionCardViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CollectionCardViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CollectionCardViewHolder(
        private val binding: ItemCollectionCardBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(card: CollectionCardUiModel) {
            with(binding) {
                ivCardImage.loadImage(card.imageUrl, cornerRadius = 8f)
                tvCardName.text = card.name
                tvQuantity.text = "Quantity: ${card.quantityDisplay}"
                tvCondition.text = "Condition: ${card.condition}"
                tvTotalValue.text = "Total: ${card.totalValueFormatted}"

                root.setOnClickListener {
                    onCardClick(card.cardId)
                }
            }
        }
    }

    private class CollectionCardDiffCallback : DiffUtil.ItemCallback<CollectionCardUiModel>() {
        override fun areItemsTheSame(oldItem: CollectionCardUiModel, newItem: CollectionCardUiModel): Boolean {
            return oldItem.cardId == newItem.cardId
        }

        override fun areContentsTheSame(oldItem: CollectionCardUiModel, newItem: CollectionCardUiModel): Boolean {
            return oldItem == newItem
        }
    }
}
