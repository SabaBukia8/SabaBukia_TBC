package com.example.sababukia_tbc.presentation.screen.messenger

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.sababukia_tbc.domain.common.Resource
import com.example.sababukia_tbc.databinding.FragmentNewMessengerBinding
import com.example.sababukia_tbc.presentation.common.BaseFragment
import com.example.sababukia_tbc.presentation.common.hide
import com.example.sababukia_tbc.presentation.common.show
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NewMessengerFragment : BaseFragment<FragmentNewMessengerBinding>(
    FragmentNewMessengerBinding::inflate
) {
    private val viewModel: MessengerViewModel by viewModels()

    private val chatsAdapter by lazy {
        ChatsAdapter(::onChatClick)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        observeState()
        observeSideEffects()
    }

    override fun listeners() {
        binding.apply {
            btnSearch.setOnClickListener {
                val query = etSearch.text.toString()
                viewModel.onEvent(MessengerEvent.SearchChats(query))
            }

            etSearch.setOnEditorActionListener { _, _, _ ->
                val query = etSearch.text.toString()
                viewModel.onEvent(MessengerEvent.SearchChats(query))
                true
            }
        }
    }

    private fun initViews() {
        binding.rvChats.adapter = chatsAdapter
    }

    private fun onChatClick(chatId: Int) {
        viewModel.onEvent(MessengerEvent.OnChatClick(chatId))
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    handleChatsResource(state.chatsResource)
                    chatsAdapter.submitList(state.filteredChats)

                    binding.tvEmptyState.apply {
                        if (state.filteredChats.isEmpty() && state.chatsResource is Resource.Success) {
                            show()
                        } else {
                            hide()
                        }
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
        binding.apply {
            when (resource) {
                is Resource.Loading -> {
                    if (resource.isLoading) progressBar.show() else progressBar.hide()
                    tvError.hide()
                }
                is Resource.Success -> {
                    progressBar.hide()
                    tvError.hide()
                }
                is Resource.Error -> {
                    progressBar.hide()
                    tvError.apply {
                        text = resource.errorMessage
                        show()
                    }
                }
            }
        }
    }
}
