package com.example.sababukia_tbc.presentation.screen.messenger

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.sababukia_tbc.data.common.Resource
import com.example.sababukia_tbc.databinding.FragmentNewMessengerBinding
import com.example.sababukia_tbc.presentation.adapter.ChatsAdapter
import com.example.sababukia_tbc.presentation.common.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NewMessengerFragment : BaseFragment<FragmentNewMessengerBinding>(
    FragmentNewMessengerBinding::inflate
) {
    private val viewModel: MessengerViewModel by viewModels()
    private lateinit var chatsAdapter: ChatsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeState()
        observeSideEffects()
    }

    override fun listeners() {
        with(binding) {
            // Search button click listener
            btnSearch.setOnClickListener {
                val query = etSearch.text.toString()
                viewModel.onEvent(MessengerEvent.SearchChats(query))
            }

            // Search on IME action
            etSearch.setOnEditorActionListener { _, _, _ ->
                val query = etSearch.text.toString()
                viewModel.onEvent(MessengerEvent.SearchChats(query))
                true
            }
        }
    }

    private fun setupRecyclerView() {
        chatsAdapter = ChatsAdapter { chatId ->
            viewModel.onEvent(MessengerEvent.OnChatClick(chatId))
        }
        binding.rvChats.adapter = chatsAdapter
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    handleChatsResource(state.chatsResource)
                    chatsAdapter.submitList(state.filteredChats)

                    // Show empty state when search returns no results
                    if (state.filteredChats.isEmpty() && state.chatsResource is Resource.Success) {
                        binding.tvEmptyState.visibility = View.VISIBLE
                    } else {
                        binding.tvEmptyState.visibility = View.GONE
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
                        is MessengerSideEffect.ShowError -> {
                            Toast.makeText(
                                requireContext(),
                                sideEffect.errorMessage,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        is MessengerSideEffect.NavigateToChatDetails -> {
                            // Navigate to chat details (not implemented yet)
                            Toast.makeText(
                                requireContext(),
                                "Chat ${sideEffect.chatId} clicked",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }
    }

    private fun handleChatsResource(resource: Resource<*>) {
        when (resource) {
            is Resource.Loading -> {
                binding.progressBar.visibility = if (resource.isLoading) View.VISIBLE else View.GONE
                binding.tvError.visibility = View.GONE
            }
            is Resource.Success -> {
                binding.progressBar.visibility = View.GONE
                binding.tvError.visibility = View.GONE
            }
            is Resource.Error -> {
                binding.progressBar.visibility = View.GONE
                binding.tvError.text = resource.errorMessage
                binding.tvError.visibility = View.VISIBLE
            }
        }
    }
}
