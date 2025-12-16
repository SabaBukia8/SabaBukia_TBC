package com.example.mtgcollectionmanager.presentation.screen.collectionslist

import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.mtgcollectionmanager.databinding.FragmentCollectionsListBinding
import com.example.mtgcollectionmanager.presentation.common.BaseFragment
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CollectionsListFragment : BaseFragment<FragmentCollectionsListBinding>(
    FragmentCollectionsListBinding::inflate
) {
    private val viewModel: CollectionsListViewModel by viewModels()

    private val adapter by lazy {
        CollectionsListAdapter(
            onCollectionClick = { collectionId ->
                viewModel.onEvent(CollectionsListContract.Event.OnCollectionClick(collectionId))
            },
            onEditClick = { collectionId ->
                viewModel.onEvent(CollectionsListContract.Event.OnEditCollectionClick(collectionId))
            },
            onDeleteClick = { collectionId ->
                viewModel.onEvent(CollectionsListContract.Event.OnDeleteCollectionClick(collectionId))
            }
        )
    }

    override fun bind() {
        with(binding) {
            rvCollections.adapter = adapter
        }
        observeState()
        observeSideEffects()
    }

    override fun listeners() {
        binding.fabCreateCollection.setOnClickListener {
            viewModel.onEvent(CollectionsListContract.Event.OnCreateCollectionClick)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.onEvent(CollectionsListContract.Event.LoadCollections)
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    with(binding) {
                        progressBar.isVisible = state.isLoading
                        adapter.submitList(state.collections)
                        llEmptyState.isVisible = state.collections.isEmpty() && !state.isLoading
                        rvCollections.isVisible = state.collections.isNotEmpty()

                        if (state.isNetworkAvailable) {
                            networkStatusView.visibility = View.GONE
                        } else {
                            networkStatusView.updateNetworkStatus(
                                com.example.mtgcollectionmanager.data.remote.util.NetworkConnectivityManager.NetworkState.Unavailable
                            )
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
                        is CollectionsListContract.SideEffect.NavigateToCollection -> {
                            findNavController().navigate(
                                CollectionsListFragmentDirections
                                    .actionCollectionsListFragmentToCollectionFragment(sideEffect.collectionId)
                            )
                        }

                        is CollectionsListContract.SideEffect.ShowCreateCollectionDialog -> {
                            showCreateCollectionDialog()
                        }

                        is CollectionsListContract.SideEffect.ShowEditCollectionDialog -> {
                            showEditCollectionDialog(sideEffect.collectionId)
                        }

                        is CollectionsListContract.SideEffect.ShowError -> {
                            showMessage(sideEffect.message.asString(requireContext()))
                        }

                        is CollectionsListContract.SideEffect.ShowSuccess -> {
                            showMessage(sideEffect.message.asString(requireContext()))
                        }
                    }
                }
            }
        }
    }

    private fun showMessage(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }

    private fun showCreateCollectionDialog() {
        CreateCollectionDialog { name, description ->
            viewModel.onEvent(CollectionsListContract.Event.CreateCollection(name, description))
        }.show(parentFragmentManager, "CreateCollectionDialog")
    }

    private fun showEditCollectionDialog(collectionId: Long) {
        val collection = viewModel.state.value.collections.find { it.id == collectionId } ?: return

        EditCollectionDialog(
            collectionId = collectionId,
            currentName = collection.name,
            currentDescription = collection.description
        ) { id, name, description ->
            viewModel.onEvent(CollectionsListContract.Event.UpdateCollection(id, name, description))
        }.show(parentFragmentManager, "EditCollectionDialog")
    }
}