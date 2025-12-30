package com.example.sababukia_tbc.presentation.screen.categories

import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sababukia_tbc.databinding.FragmentCategoryBinding
import com.example.sababukia_tbc.presentation.common.BaseFragment
import com.example.sababukia_tbc.presentation.extension.collectFlow
import com.example.sababukia_tbc.presentation.extension.collectStateFlow
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CategoryFragment : BaseFragment<FragmentCategoryBinding>(
    FragmentCategoryBinding::inflate
) {

    private val viewModel: CategoryViewModel by viewModels()

    private val categoryAdapter: CategoryAdapter by lazy {
        CategoryAdapter()
    }

    override fun bind() {
        setupRecyclerView()
        observeState()
        observeSideEffects()
    }

    override fun listeners() {
        binding.etSearch.addTextChangedListener { text ->
            viewModel.onEvent(
                CategoryContract.Event.SearchQueryChanged(text.toString())
            )
        }
    }

    private fun setupRecyclerView() {
        binding.rvCategories.apply {
            adapter = categoryAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun observeState() {
        collectStateFlow(viewModel.uiState) { state ->
            binding.progressBar.isVisible = state.isLoading
            categoryAdapter.submitList(state.filteredCategories)
        }
    }

    private fun observeSideEffects() {
        collectFlow(viewModel.sideEffect) { effect ->
            when (effect) {
                is CategoryContract.SideEffect.ShowError -> {
                    Snackbar.make(
                        binding.root,
                        effect.message,
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}
