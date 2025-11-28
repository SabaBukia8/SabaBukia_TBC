package com.example.sababukia_tbc.presentation.screen.userprofile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.sababukia_tbc.databinding.ItemUserProfileBinding

class UserProfileAdapter(
    private val onDeleteClick: (Long) -> Unit
) : ListAdapter<UserProfileModel, UserProfileAdapter.ProfileViewHolder>(ProfileDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileViewHolder {
        val binding = ItemUserProfileBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ProfileViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProfileViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ProfileViewHolder(
        private val binding: ItemUserProfileBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(profile: UserProfileModel) {
            binding.apply {
                tvFullName.text = "${profile.firstName} ${profile.lastName}"
                tvEmail.text = profile.email

                btnDelete.setOnClickListener {
                    onDeleteClick(profile.id)
                }
            }
        }
    }

    private class ProfileDiffCallback : DiffUtil.ItemCallback<UserProfileModel>() {
        override fun areItemsTheSame(oldItem: UserProfileModel, newItem: UserProfileModel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: UserProfileModel, newItem: UserProfileModel): Boolean {
            return oldItem == newItem
        }
    }
}
