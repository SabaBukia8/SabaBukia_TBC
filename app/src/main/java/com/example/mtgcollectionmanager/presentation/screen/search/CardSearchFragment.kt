package com.example.mtgcollectionmanager.presentation.screen.search

import android.os.Bundle
import android.view.inputmethod.EditorInfo
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mtgcollectionmanager.R
import com.example.mtgcollectionmanager.databinding.FragmentCardSearchBinding
import com.example.mtgcollectionmanager.presentation.common.BaseFragment
import com.example.mtgcollectionmanager.presentation.common.hide
import com.example.mtgcollectionmanager.presentation.common.show
import com.example.mtgcollectionmanager.presentation.common.showErrorSnackbar
import com.example.mtgcollectionmanager.presentation.screen.search.adapter.CardSearchAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CardSearchFragment : BaseFragment<FragmentCardSearchBinding>(
    FragmentCardSearchBinding::inflate
) {
    private val viewModel: CardSearchViewModel by viewModels()
    private val adapter by lazy {
        CardSearchAdapter { cardId ->
            viewModel.onEvent(CardSearchContract.Event.CardClicked(cardId))
        }
    }

    override fun bind() {
        setupRecyclerView()
        observeState()
        observeSideEffects()
    }

    override fun listeners() {
        with(binding) {
            etSearch.doAfterTextChanged { text ->
                viewModel.onEvent(CardSearchContract.Event.SearchQueryChanged(text.toString()))
            }

            etSearch.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    viewModel.onEvent(CardSearchContract.Event.SearchClicked)
                    true
                } else false
            }

            btnSearch.setOnClickListener {
                viewModel.onEvent(CardSearchContract.Event.SearchClicked)
            }

            btnFilters.setOnClickListener {
                viewModel.onEvent(CardSearchContract.Event.FiltersClicked)
            }
        }
    }

    private fun setupRecyclerView() {
        binding.rvCards.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@CardSearchFragment.adapter
        }
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    with(binding) {
                        when {
                            state.isLoading -> {
                                progressBar.show()
                                rvCards.hide()
                                llEmptyState.hide()
                            }
                            state.cards.isEmpty() && state.hasSearched -> {
                                progressBar.hide()
                                rvCards.hide()
                                llEmptyState.show()
                            }
                            state.cards.isNotEmpty() -> {
                                progressBar.hide()
                                llEmptyState.hide()
                                rvCards.show()
                                adapter.submitList(state.cards)
                            }
                            else -> {
                                progressBar.hide()
                                rvCards.hide()
                                llEmptyState.show()
                            }
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
                        is CardSearchContract.SideEffect.NavigateToCardDetails -> {
                            navigateToCardDetails(sideEffect.cardId)
                        }

                        is CardSearchContract.SideEffect.ShowError -> {
                            binding.root.showErrorSnackbar(
                                sideEffect.message.asString(
                                    requireContext()
                                )
                            )
                        }

                        is CardSearchContract.SideEffect.ShowFiltersDialog -> {
                            showFiltersDialog(sideEffect.currentFilters)
                        }
                    }
                }
            }
        }
    }

    private fun navigateToCardDetails(cardId: String) {
        val currentDestId = findNavController().currentDestination?.id

        when (currentDestId) {
            R.id.searchFragment -> {
                val bundle = Bundle().apply {
                    putString("cardId", cardId)
                    putLong("collectionId", viewModel.collectionId)
                }
                findNavController().navigate(
                    R.id.action_searchFragment_to_cardDetailsFragment,
                    bundle
                )
            }

            else -> {
                findNavController().navigate(
                    CardSearchFragmentDirections.actionCardSearchFragmentToCardDetailsFragment(
                        cardId = cardId,
                        collectionId = viewModel.collectionId
                    )
                )
            }
        }
    }

    private fun showFiltersDialog(currentFilters: com.example.mtgcollectionmanager.presentation.screen.search.model.SearchFilters) {
        val dialog = com.example.mtgcollectionmanager.presentation.screen.search.dialog.SearchFiltersDialog(
            currentFilters = currentFilters,
            onFiltersApplied = { filters ->
                viewModel.onEvent(CardSearchContract.Event.FiltersApplied(filters))
            }
        )
        dialog.show(parentFragmentManager, "SearchFiltersDialog")
    }
}
