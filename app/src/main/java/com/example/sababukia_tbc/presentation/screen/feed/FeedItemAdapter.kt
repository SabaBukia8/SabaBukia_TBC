package com.example.sababukia_tbc.presentation.screen.feed

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintSet
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.sababukia_tbc.R
import com.example.sababukia_tbc.databinding.ItemFeedStoriesBinding
import com.example.sababukia_tbc.databinding.ItemPostBinding
import com.example.sababukia_tbc.databinding.LayoutPostImagesBinding
import com.example.sababukia_tbc.presentation.common.hide
import com.example.sababukia_tbc.presentation.common.loadImage
import com.example.sababukia_tbc.presentation.common.show
import com.example.sababukia_tbc.presentation.model.FeedItem
import com.example.sababukia_tbc.presentation.model.PostUiModel
import com.example.sababukia_tbc.presentation.model.StoryUiModel
import com.example.sababukia_tbc.presentation.screen.feed.StoryAdapter

class FeedItemAdapter : ListAdapter<FeedItem, RecyclerView.ViewHolder>(FeedItemDiffCallback()) {

    companion object {
        private const val VIEW_TYPE_STORIES = 0
        private const val VIEW_TYPE_POST = 1
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is FeedItem.Stories -> VIEW_TYPE_STORIES
            is FeedItem.Post -> VIEW_TYPE_POST
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_STORIES -> {
                val binding = ItemFeedStoriesBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                StoriesViewHolder(binding)
            }
            VIEW_TYPE_POST -> {
                val binding = ItemPostBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                PostViewHolder(binding)
            }
            else -> throw IllegalArgumentException("Unknown view type: $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is FeedItem.Stories -> (holder as StoriesViewHolder).bind(item.stories)
            is FeedItem.Post -> (holder as PostViewHolder).bind(item.post)
        }
    }

    inner class StoriesViewHolder(private val binding: ItemFeedStoriesBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val storyAdapter = StoryAdapter()

        init {
            binding.rvStories.apply {
                adapter = storyAdapter
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            }
        }

        fun bind(stories: List<StoryUiModel>) {
            storyAdapter.submitList(stories)
        }
    }

    inner class PostViewHolder(private val binding: ItemPostBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(post: PostUiModel) = with(binding) {
            ivUserAvatar.loadImage(post.avatar, isCircle = true)
            tvUserName.text = post.fullName
            tvPostDate.text = post.postDateFormatted
            tvPostDescription.text = post.postDesc
            tvCommentsCount.text = post.commentsCountText
            tvLikesCount.text = post.likesCountText

            val imagesBinding = LayoutPostImagesBinding.bind(root.findViewById(R.id.layout_post_images))

            if (post.images.isNotEmpty()) {
                imagesBinding.root.show()

                with(imagesBinding) {
                    when (post.images.size) {
                        1 -> {
                            ivImage1.show()
                            ivImage2.hide()
                            ivImage3.hide()

                            ConstraintSet().apply {
                                clone(root)
                                clear(R.id.iv_image_1, ConstraintSet.END)
                                connect(R.id.iv_image_1, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)
                                setDimensionRatio(R.id.iv_image_1, "1:1.3")
                                applyTo(root)
                            }

                            ivImage1.loadImage(post.images[0], cornerRadius = 64f)
                        }
                        2 -> {
                            ivImage1.show()
                            ivImage2.show()
                            ivImage3.hide()

                            ConstraintSet().apply {
                                clone(root)
                                clear(R.id.iv_image_2, ConstraintSet.BOTTOM)
                                connect(R.id.iv_image_2, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM)
                                setHorizontalWeight(R.id.iv_image_1, 0.48f)
                                setHorizontalWeight(R.id.iv_image_2, 0.48f)
                                setDimensionRatio(R.id.iv_image_1, "1:1.3")
                                setDimensionRatio(R.id.iv_image_2, "1:1.3")
                                applyTo(root)
                            }

                            ivImage1.loadImage(post.images[0], cornerRadius = 64f)
                            ivImage2.loadImage(post.images[1], cornerRadius = 64f)
                        }
                        else -> {
                            ivImage1.show()
                            ivImage2.show()
                            ivImage3.show()

                            ConstraintSet().apply {
                                clone(root)
                                setHorizontalWeight(R.id.iv_image_1, 0.48f)
                                setDimensionRatio(R.id.iv_image_1, "1:1.3")
                                setDimensionRatio(R.id.iv_image_2, "1:0.5")
                                setDimensionRatio(R.id.iv_image_3, "1:0.5")
                                applyTo(root)
                            }

                            ivImage1.loadImage(post.images[0], cornerRadius = 64f)
                            ivImage2.loadImage(post.images[1], cornerRadius = 64f)
                            ivImage3.loadImage(post.images[2], cornerRadius = 64f)
                        }
                    }
                }
            } else {
                imagesBinding.root.hide()
            }

            if (post.canComment) {
                dividerComment.show()
                layoutWriteComment.show()
                ivCommentAvatar.loadImage(post.avatar, isCircle = true)
            } else {
                dividerComment.hide()
                layoutWriteComment.hide()
            }
        }
    }

    private class FeedItemDiffCallback : DiffUtil.ItemCallback<FeedItem>() {
        override fun areItemsTheSame(oldItem: FeedItem, newItem: FeedItem): Boolean {
            return when {
                oldItem is FeedItem.Stories && newItem is FeedItem.Stories -> true
                oldItem is FeedItem.Post && newItem is FeedItem.Post ->
                    oldItem.post.id == newItem.post.id
                else -> false
            }
        }

        override fun areContentsTheSame(oldItem: FeedItem, newItem: FeedItem): Boolean {
            return oldItem == newItem
        }
    }
}