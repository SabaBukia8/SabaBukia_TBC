package com.example.mtgcollectionmanager.presentation.screen.cardprintings.dialog

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mtgcollectionmanager.databinding.ItemCardPrintingBinding
import com.example.mtgcollectionmanager.presentation.common.loadImage
import com.example.mtgcollectionmanager.presentation.model.CardUiModel

class CardPrintingsAdapter(
    private val currentCardId: String?,
    private val onPrintingClick: (String) -> Unit
) : ListAdapter<CardUiModel, CardPrintingsAdapter.PrintingViewHolder>(PrintingDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PrintingViewHolder {
        val binding = ItemCardPrintingBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PrintingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PrintingViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class PrintingViewHolder(
        private val binding: ItemCardPrintingBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(card: CardUiModel) {
            with(binding) {
                ivCardImage.loadImage(card.imageUrl, cornerRadius = 8f)

                tvSetName.text = card.setName
                tvSetCode.text = "${card.setCode.uppercase()} #${card.collectorNumber}"
                tvRarity.text = card.rarity
                tvPrice.text = card.priceFormatted
                tvReleaseDate.text = "Released: ${card.releasedAt}"


                if (card.cardId == currentCardId) {
                    root.strokeWidth = 4
                    root.strokeColor = androidx.core.content.ContextCompat.getColor(root.context, android.R.color.holo_blue_dark)
                } else {
                    root.strokeWidth = 0
                }

                root.setOnClickListener {
                    onPrintingClick(card.cardId)
                }
            }
        }
    }

    private class PrintingDiffCallback : DiffUtil.ItemCallback<CardUiModel>() {
        override fun areItemsTheSame(oldItem: CardUiModel, newItem: CardUiModel): Boolean {
            return oldItem.cardId == newItem.cardId
        }

        override fun areContentsTheSame(oldItem: CardUiModel, newItem: CardUiModel): Boolean {
            return oldItem == newItem
        }
    }
}