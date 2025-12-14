package com.example.mtgcollectionmanager.presentation.screen.search.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mtgcollectionmanager.databinding.ItemCardBinding
import com.example.mtgcollectionmanager.presentation.common.loadImage
import com.example.mtgcollectionmanager.presentation.model.CardUiModel

class CardSearchAdapter(
    private val onCardClick: (String) -> Unit
) : ListAdapter<CardUiModel, CardSearchAdapter.CardViewHolder>(CardDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val binding = ItemCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CardViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CardViewHolder(
        private val binding: ItemCardBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(card: CardUiModel) {
            with(binding) {
                ivCardImage.loadImage(card.imageUrl, cornerRadius = 8f)
                tvCardName.text = card.name

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

                tvSetInfo.text = card.setInfo
                tvPrice.text = card.priceFormatted

                root.setOnClickListener {
                    onCardClick(card.cardId)
                }
            }
        }
    }

    private class CardDiffCallback : DiffUtil.ItemCallback<CardUiModel>() {
        override fun areItemsTheSame(oldItem: CardUiModel, newItem: CardUiModel): Boolean {
            return oldItem.cardId == newItem.cardId
        }

        override fun areContentsTheSame(oldItem: CardUiModel, newItem: CardUiModel): Boolean {
            return oldItem == newItem
        }
    }
}
