package com.example.sababukia_tbc.presentation.screen.feed

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.sababukia_tbc.databinding.ItemStoryBinding
import com.example.sababukia_tbc.presentation.common.loadImage
import com.example.sababukia_tbc.presentation.model.StoryUiModel

class StoryAdapter : ListAdapter<StoryUiModel, StoryAdapter.StoryViewHolder>(StoryDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val binding = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class StoryViewHolder(private val binding: ItemStoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(story: StoryUiModel) = with(binding) {
            ivStoryCover.loadImage(story.cover, cornerRadius = 64f)
            tvStoryTitle.text = story.title
        }
    }

    private class StoryDiffCallback : DiffUtil.ItemCallback<StoryUiModel>() {
        override fun areItemsTheSame(oldItem: StoryUiModel, newItem: StoryUiModel): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: StoryUiModel, newItem: StoryUiModel): Boolean =
            oldItem == newItem
    }
}