package com.example.sababukia_tbc.presentation.screen.userlist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.sababukia_tbc.R
import com.example.sababukia_tbc.databinding.ItemUserListBinding
import com.example.sababukia_tbc.domain.model.UserListItem
import com.example.sababukia_tbc.presentation.common.loadImage

class UserListAdapter : ListAdapter<UserListItem, UserListAdapter.UserViewHolder>(UserDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder =
        UserViewHolder(
            ItemUserListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class UserViewHolder(private val binding: ItemUserListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(user: UserListItem) {
            binding.apply {
                tvFullName.text = user.fullName
                tvEmail.text = user.email
                tvLastActive.text = user.lastActiveDescription

                val (statusText, statusColor) = getActivationStatusInfo(user.activationStatus)
                tvActivationStatus.apply {
                    text = statusText
                    setBackgroundColor(statusColor)
                }

                ivProfileImage.loadImage(user.profileImageUrl)
            }
        }

        private fun getActivationStatusInfo(status: Int): Pair<String, Int> {
            return when {
                status <= 0 -> {
                    binding.root.context.getString(R.string.user_status_not_activated) to
                            ContextCompat.getColor(binding.root.context, R.color.status_not_activated)
                }
                status == 1 -> {
                    binding.root.context.getString(R.string.user_status_online) to
                            ContextCompat.getColor(binding.root.context, R.color.status_online)
                }
                status == 2 -> {
                    binding.root.context.getString(R.string.user_status_active_minutes) to
                            ContextCompat.getColor(binding.root.context, R.color.status_active_minutes)
                }
                status in 3..22 -> {
                    binding.root.context.getString(R.string.user_status_active_hours) to
                            ContextCompat.getColor(binding.root.context, R.color.status_active_hours)
                }
                else -> {
                    binding.root.context.getString(R.string.user_status_inactive) to
                            ContextCompat.getColor(binding.root.context, R.color.status_inactive)
                }
            }
        }
    }

    class UserDiffCallback : DiffUtil.ItemCallback<UserListItem>() {
        override fun areItemsTheSame(oldItem: UserListItem, newItem: UserListItem): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: UserListItem, newItem: UserListItem): Boolean =
            oldItem == newItem
    }
}
