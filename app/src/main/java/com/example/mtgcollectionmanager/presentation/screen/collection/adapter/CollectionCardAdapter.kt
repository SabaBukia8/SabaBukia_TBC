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
    private val onCardClick: (String) -> Unit,
    private val onCardLongClick: ((String) -> Unit)? = null
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
                tvQuantityBadge.text = card.quantityDisplay

                // Render mana symbols
                com.example.mtgcollectionmanager.presentation.common.ManaSymbolRenderer.renderManaSymbols(
                    root.context,
                    card.manaCost,
                    llManaSymbols
                )

                // Set rarity color strip
                vRarityStrip.setBackgroundColor(
                    com.example.mtgcollectionmanager.presentation.common.RarityColorHelper.getRarityColor(
                        root.context,
                        card.rarity
                    )
                )

                tvCondition.text = card.condition
                tvTotalValue.text = card.totalValueFormatted

                root.setOnClickListener {
                    onCardClick(card.cardId)
                }

                root.setOnLongClickListener {
                    onCardLongClick?.invoke(card.cardId)
                    true
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
