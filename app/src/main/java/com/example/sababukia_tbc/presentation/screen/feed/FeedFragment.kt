package com.example.sababukia_tbc.presentation.screen.feed

import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sababukia_tbc.databinding.FragmentFeedBinding
import com.example.sababukia_tbc.presentation.common.BaseFragment
import com.example.sababukia_tbc.presentation.common.hide
import com.example.sababukia_tbc.presentation.common.show
import com.example.sababukia_tbc.presentation.model.FeedItem
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FeedFragment : BaseFragment<FragmentFeedBinding>(FragmentFeedBinding::inflate) {

    private val viewModel: FeedViewModel by viewModels()
    private val feedAdapter by lazy { FeedItemAdapter() }

    override fun bind() {
        setupRecyclerView()
        observeState()
        observeSideEffects()
    }

    private fun setupRecyclerView() = with(binding) {
        rvFeed.apply {
            adapter = feedAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    with(binding) {
                        if (state.isLoading) progressBar.show() else progressBar.hide()

                        val feedItems = mutableListOf<FeedItem>()
                        if (state.stories.isNotEmpty()) {
                            feedItems.add(FeedItem.Stories(state.stories))
                        }
                        feedItems.addAll(state.posts.map { FeedItem.Post(it) })

                        feedAdapter.submitList(feedItems)
                    }
                }
            }
        }
    }

    private fun observeSideEffects() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.sideEffect.collect { sideEffect ->
                    when (sideEffect) {
                        is FeedSideEffect.ShowError -> {
                            val errorMessage = sideEffect.message ?: getString(com.example.sababukia_tbc.R.string.error_unknown)
                            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }
}
