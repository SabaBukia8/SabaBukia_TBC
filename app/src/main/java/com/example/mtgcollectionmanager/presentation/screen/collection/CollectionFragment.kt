package com.example.mtgcollectionmanager.presentation.screen.collection

import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mtgcollectionmanager.R
import com.example.mtgcollectionmanager.databinding.FragmentCollectionBinding
import com.example.mtgcollectionmanager.presentation.common.BaseFragment
import com.example.mtgcollectionmanager.presentation.common.hide
import com.example.mtgcollectionmanager.presentation.common.show
import com.example.mtgcollectionmanager.presentation.common.showErrorSnackbar
import com.example.mtgcollectionmanager.presentation.screen.collection.adapter.CollectionCardAdapter
import com.example.mtgcollectionmanager.presentation.screen.collection.bottomsheet.EditCardBottomSheet
import com.example.mtgcollectionmanager.presentation.screen.collection.dialog.ColorFilterDialog
import com.example.mtgcollectionmanager.presentation.screen.collection.dialog.SetFilterDialog
import com.example.mtgcollectionmanager.presentation.screen.collection.drawer.CategoryDrawerAdapter
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CollectionFragment : BaseFragment<FragmentCollectionBinding>(
    FragmentCollectionBinding::inflate
) {
    private val viewModel: CollectionViewModel by viewModels()
    private val adapter by lazy {
        CollectionCardAdapter(
            onCardClick = { cardId ->
                viewModel.onEvent(CollectionContract.Event.CardClicked(cardId))
            },
            onCardLongClick = { cardId ->
                viewModel.onEvent(CollectionContract.Event.EditCardClicked(cardId))
            }
        )
    }

    private val drawerAdapter by lazy {
        CategoryDrawerAdapter { categoryId ->
            viewModel.onEvent(CollectionContract.Event.CategoryFilterClicked(categoryId))
            binding.drawerLayout.closeDrawers()
        }
    }

    override fun bind() {
        setupRecyclerView()
        setupDrawer()
        setupToolbar()
        observeState()
        observeSideEffects()
    }

    override fun listeners() {
        with(binding) {
            btnViewAll.setOnClickListener {
                viewModel.onEvent(CollectionContract.Event.ViewModeChanged(CollectionContract.ViewMode.ALL))
                viewModel.onEvent(CollectionContract.Event.ColorFilterApplied(emptyMap()))
                viewModel.onEvent(CollectionContract.Event.SetFilterApplied(emptyMap()))
            }

            btnViewByColor.setOnClickListener {
                viewModel.onEvent(CollectionContract.Event.ColorFilterClicked)
            }

            btnViewBySet.setOnClickListener {
                viewModel.onEvent(CollectionContract.Event.SetFilterClicked)
            }

            fabSearch.setOnClickListener {
                viewModel.onEvent(CollectionContract.Event.SearchClicked)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.onEvent(CollectionContract.Event.RefreshCollection)
    }

    private fun setupRecyclerView() {
        binding.rvCollection.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@CollectionFragment.adapter
        }

        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val card = adapter.currentList[position]
                viewModel.onEvent(CollectionContract.Event.DeleteCardClicked(card.cardId))
                // Restore the item immediately to prevent visual glitch
                adapter.notifyItemChanged(position)
            }
        })

        itemTouchHelper.attachToRecyclerView(binding.rvCollection)
    }

    private fun setupDrawer() {
        val drawerRecyclerView = binding.root.findViewById<RecyclerView>(R.id.rvCategories)
        drawerRecyclerView?.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = drawerAdapter
        }

        val btnManageCategories = binding.root.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnManageCategories)
        btnManageCategories?.setOnClickListener {
            viewModel.onEvent(CollectionContract.Event.ManageCategoriesClicked)
            binding.drawerLayout.closeDrawers()
        }
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            binding.drawerLayout.open()
        }

        binding.toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_manage_collections -> {
                    viewModel.onEvent(CollectionContract.Event.ManageCollectionsClicked)
                    true
                }
                R.id.action_logout -> {
                    viewModel.onEvent(CollectionContract.Event.LogoutClicked)
                    true
                }
                else -> false
            }
        }
        binding.toolbar.inflateMenu(R.menu.menu_collection)
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    with(binding) {
                        when {
                            state.isLoading -> {
                                progressBar.show()
                                rvCollection.hide()
                                llEmptyState.hide()
                            }
                            state.cards.isEmpty() -> {
                                progressBar.hide()
                                rvCollection.hide()
                                llEmptyState.show()
                            }
                            else -> {
                                progressBar.hide()
                                llEmptyState.hide()
                                rvCollection.show()
                                adapter.submitList(state.cards)
                            }
                        }

                        tvTotalValue.text = getString(R.string.total_value, state.totalValue)
                        tvTotalCards.text = getString(R.string.total_cards, state.totalCards)

                        // Update drawer categories
                        drawerAdapter.submitList(state.categories)
                        drawerAdapter.setSelectedCategory(state.selectedCategoryId)
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
                        is CollectionContract.SideEffect.NavigateToCardDetails -> {
                            findNavController().navigate(
                                CollectionFragmentDirections.actionCollectionFragmentToCardDetailsFragment(
                                    cardId = sideEffect.cardId,
                                    collectionId = viewModel.collectionId
                                )
                            )
                        }
                        is CollectionContract.SideEffect.NavigateToSearch -> {
                            findNavController().navigate(
                                CollectionFragmentDirections.actionCollectionFragmentToCardSearchFragment(
                                    collectionId = viewModel.collectionId
                                )
                            )
                        }
                        is CollectionContract.SideEffect.NavigateToLogin -> {
                            findNavController().navigate(
                                CollectionFragmentDirections.actionCollectionFragmentToLoginFragment()
                            )
                        }
                        is CollectionContract.SideEffect.ShowError -> {
                            binding.root.showErrorSnackbar(sideEffect.message.asString(requireContext()))
                        }
                        is CollectionContract.SideEffect.ShowSuccess -> {
                            Snackbar.make(
                                binding.root,
                                sideEffect.message.asString(requireContext()),
                                Snackbar.LENGTH_SHORT
                            ).show()
                        }
                        is CollectionContract.SideEffect.ShowColorFilterDialog -> {
                            showColorFilterDialog(sideEffect.colorFilters)
                        }
                        is CollectionContract.SideEffect.ShowSetFilterDialog -> {
                            showSetFilterDialog(sideEffect.setFilters)
                        }
                        is CollectionContract.SideEffect.ShowDeleteConfirmation -> {
                            showDeleteConfirmationDialog(sideEffect.cardId, sideEffect.cardName)
                        }
                        is CollectionContract.SideEffect.ShowEditCardDialog -> {
                            showEditCardDialog(sideEffect.card)
                        }
                        is CollectionContract.SideEffect.ShowManageCollectionsDialog -> {
                            showManageCollectionsDialog(sideEffect.collections)
                        }
                        is CollectionContract.SideEffect.ShowManageCategoriesDialog -> {
                            showManageCategoriesDialog(sideEffect.categories)
                        }
                    }
                }
            }
        }
    }

    private fun showColorFilterDialog(colorFilters: Map<String, CollectionContract.FilterState>) {
        ColorFilterDialog.newInstance(colorFilters) { filters ->
            viewModel.onEvent(CollectionContract.Event.ColorFilterApplied(filters))
        }.show(parentFragmentManager, "ColorFilterDialog")
    }

    private fun showSetFilterDialog(setFilters: Map<String, CollectionContract.FilterState>) {
        SetFilterDialog.newInstance(setFilters) { filters ->
            viewModel.onEvent(CollectionContract.Event.SetFilterApplied(filters))
        }.show(parentFragmentManager, "SetFilterDialog")
    }

    private fun showDeleteConfirmationDialog(cardId: String, cardName: String) {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.delete_card_title)
            .setMessage(getString(R.string.delete_card_message, cardName))
            .setPositiveButton(R.string.delete) { _, _ ->
                viewModel.deleteCard(cardId)
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun showEditCardDialog(card: com.example.mtgcollectionmanager.presentation.model.CollectionCardUiModel) {
        // Extract categories from drawer items
        val categories = viewModel.state.value.categories.mapNotNull { item ->
            when (item) {
                is com.example.mtgcollectionmanager.presentation.screen.collection.drawer.CategoryDrawerItem.Category -> {
                    com.example.mtgcollectionmanager.domain.model.Category(
                        id = item.id,
                        collectionId = 1L, // Hardcoded
                        name = item.name,
                        color = item.color,
                        createdDate = 0L,
                        cardCount = item.cardCount
                    )
                }
                else -> null
            }
        }

        EditCardBottomSheet.newInstance(card, categories) { quantity, condition, notes, categoryId ->
            viewModel.onEvent(
                CollectionContract.Event.SaveCardDetails(
                    cardId = card.cardId,
                    quantity = quantity,
                    condition = condition,
                    notes = notes,
                    categoryId = categoryId
                )
            )
        }.show(parentFragmentManager, "EditCardBottomSheet")
    }

    private fun showManageCollectionsDialog(collections: List<com.example.mtgcollectionmanager.presentation.model.CollectionUi>) {
        com.example.mtgcollectionmanager.presentation.screen.collection.bottomsheet.ManageCollectionsBottomSheet.newInstance(
            collections = collections,
            onCreateCollection = { name, description ->
                viewModel.onEvent(CollectionContract.Event.CreateCollection(name, description))
            },
            onEditCollection = { id, name, description ->
                viewModel.onEvent(CollectionContract.Event.UpdateCollection(id, name, description))
            },
            onDeleteCollection = { id ->
                viewModel.onEvent(CollectionContract.Event.DeleteCollection(id))
            }
        ).show(parentFragmentManager, "ManageCollectionsBottomSheet")
    }

    private fun showManageCategoriesDialog(categories: List<com.example.mtgcollectionmanager.domain.model.Category>) {
        com.example.mtgcollectionmanager.presentation.screen.collection.bottomsheet.ManageCategoriesBottomSheet.newInstance(
            categories = categories,
            onCreateCategory = { name, color ->
                viewModel.onEvent(CollectionContract.Event.CreateCategory(name, color))
            },
            onEditCategory = { id, name, color ->
                viewModel.onEvent(CollectionContract.Event.UpdateCategory(id, name, color))
            },
            onDeleteCategory = { id ->
                viewModel.onEvent(CollectionContract.Event.DeleteCategory(id))
            }
        ).show(parentFragmentManager, "ManageCategoriesBottomSheet")
    }
}
