package com.example.sababukia_tbc.presentation.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.sababukia_tbc.R
import com.example.sababukia_tbc.databinding.ItemChatBinding
import com.example.sababukia_tbc.domain.model.ChatItem
import com.example.sababukia_tbc.domain.model.MessageType

class ChatsAdapter(
    private val onChatClick: (Int) -> Unit
) : ListAdapter<ChatItem, ChatsAdapter.ChatViewHolder>(ChatDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val binding = ItemChatBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ChatViewHolder(binding, onChatClick)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ChatViewHolder(
        private val binding: ItemChatBinding,
        private val onChatClick: (Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(chatItem: ChatItem) {
            with(binding) {
                // Set owner name
                tvOwnerName.text = chatItem.owner

                // Set last message
                tvLastMessage.text = chatItem.lastMessage

                // Set time
                tvTime.text = chatItem.lastActive

                // Handle typing indicator
                if (chatItem.isTyping) {
                    tvTyping.visibility = View.VISIBLE
                } else {
                    tvTyping.visibility = View.GONE
                }

                // Handle message type icon
                when (chatItem.messageType) {
                    MessageType.VOICE -> {
                        ivMessageTypeIcon.visibility = View.VISIBLE
                        ivMessageTypeIcon.setImageResource(R.drawable.ic_voice_message)
                        tvLastMessage.text = root.context.getString(R.string.sent_a_voice_message)
                    }
                    MessageType.FILE -> {
                        ivMessageTypeIcon.visibility = View.VISIBLE
                        ivMessageTypeIcon.setImageResource(R.drawable.ic_attachment)
                        tvLastMessage.text = root.context.getString(R.string.sent_an_attachment)
                    }
                    MessageType.TEXT -> {
                        ivMessageTypeIcon.visibility = View.GONE
                    }
                }

                // Handle unread messages badge
                if (chatItem.unreadMessages > 0) {
                    tvUnreadBadge.visibility = View.VISIBLE
                    tvUnreadBadge.text = chatItem.unreadMessages.toString()
                } else {
                    tvUnreadBadge.visibility = View.GONE
                }

                // Load avatar
                if (!chatItem.image.isNullOrEmpty()) {
                    Glide.with(ivAvatar.context)
                        .load(chatItem.image)
                        .placeholder(getAvatarColorForId(chatItem.id))
                        .error(getAvatarColorForId(chatItem.id))
                        .circleCrop()
                        .into(ivAvatar)
                } else {
                    // Set colored background for avatar
                    ivAvatar.setBackgroundColor(getAvatarColorForId(chatItem.id))
                    ivAvatar.setImageDrawable(null)
                }

                // Set click listener
                root.setOnClickListener {
                    onChatClick(chatItem.id)
                }
            }
        }

        private fun getAvatarColorForId(id: Int): Int {
            val colors = listOf(
                Color.parseColor("#FFB950"), // Yellow
                Color.parseColor("#00D9A3"), // Green
                Color.parseColor("#FF6B6B")  // Red
            )
            return colors[id % colors.size]
        }
    }

    private class ChatDiffCallback : DiffUtil.ItemCallback<ChatItem>() {
        override fun areItemsTheSame(oldItem: ChatItem, newItem: ChatItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ChatItem, newItem: ChatItem): Boolean {
            return oldItem == newItem
        }
    }
}
