package com.example.sababukia_tbc.presentation.screen.statistics

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil3.load
import coil3.request.crossfade
import coil3.request.error
import coil3.request.placeholder
import com.example.sababukia_tbc.R
import com.example.sababukia_tbc.databinding.ItemWorkspaceCardBinding
import com.example.sababukia_tbc.presentation.model.WorkspaceUiModel

class WorkspaceAdapter : ListAdapter<WorkspaceUiModel, WorkspaceAdapter.WorkspaceViewHolder>(WorkspaceDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkspaceViewHolder {
        val binding = ItemWorkspaceCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return WorkspaceViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WorkspaceViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class WorkspaceViewHolder(
        private val binding: ItemWorkspaceCardBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(workspace: WorkspaceUiModel) {
            binding.apply {
                ivWorkspace.load(workspace.image) {
                    crossfade(true)
                    placeholder(R.drawable.ic_image_placeholder)
                    error(R.drawable.ic_image_placeholder)
                }

                tvLocation.text = workspace.location
                tvAltitude.text = workspace.altitudeM.toString()
                tvTitle.text = workspace.title
                tvPrice.text = "$${workspace.price}"
                ratingBarStars.rating = workspace.stars.toFloat()
            }
        }
    }

    private class WorkspaceDiffCallback : DiffUtil.ItemCallback<WorkspaceUiModel>() {
        override fun areItemsTheSame(oldItem: WorkspaceUiModel, newItem: WorkspaceUiModel): Boolean {
            return oldItem.title == newItem.title
        }

        override fun areContentsTheSame(oldItem: WorkspaceUiModel, newItem: WorkspaceUiModel): Boolean {
            return oldItem == newItem
        }
    }
}
