package com.example.sababukia_tbc.presentation.screen.users

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.sababukia_tbc.R
import com.example.sababukia_tbc.databinding.ItemUserBinding
import com.example.sababukia_tbc.presentation.screen.users.model.UserUiModel

class UsersPagingAdapter(
    private val onUserClick: (Int) -> Unit
) : PagingDataAdapter<UserUiModel, UsersPagingAdapter.UserViewHolder>(UserDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = ItemUserBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return UserViewHolder(binding, onUserClick)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    class UserViewHolder(
        private val binding: ItemUserBinding,
        private val onUserClick: (Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(user: UserUiModel) {
            binding.apply {
                tvUserName.text = user.fullName
                tvUserEmail.text = user.email

                Glide.with(ivUserAvatar.context)
                    .load(user.avatar)
                    .placeholder(R.drawable.ic_person)
                    .error(R.drawable.ic_person)
                    .circleCrop()
                    .into(ivUserAvatar)

                root.setOnClickListener {
                    onUserClick(user.id)
                }
            }
        }
    }

    private class UserDiffCallback : DiffUtil.ItemCallback<UserUiModel>() {
        override fun areItemsTheSame(oldItem: UserUiModel, newItem: UserUiModel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: UserUiModel, newItem: UserUiModel): Boolean {
            return oldItem == newItem
        }
    }
}
