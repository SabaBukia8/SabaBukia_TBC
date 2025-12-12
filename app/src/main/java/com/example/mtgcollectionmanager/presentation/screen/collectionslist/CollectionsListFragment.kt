package com.example.mtgcollectionmanager.presentation.screen.collectionslist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.mtgcollectionmanager.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CollectionsListFragment : Fragment() {

    private val viewModel: CollectionsListViewModel by viewModels()
    private lateinit var rvCollections: RecyclerView
    private lateinit var fabCreateCollection: FloatingActionButton
    private lateinit var progressBar: View
    private lateinit var tvEmptyState: View

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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_collections_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvCollections = view.findViewById(R.id.rvCollections)
        fabCreateCollection = view.findViewById(R.id.fabCreateCollection)
        progressBar = view.findViewById(R.id.progressBar)
        tvEmptyState = view.findViewById(R.id.tvEmptyState)

        rvCollections.adapter = adapter

        fabCreateCollection.setOnClickListener {
            viewModel.onEvent(CollectionsListContract.Event.OnCreateCollectionClick)
        }

        observeState()
        observeSideEffects()
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    progressBar.isVisible = state.isLoading

                    adapter.submitList(state.collections)

                    tvEmptyState.isVisible = state.collections.isEmpty() && !state.isLoading
                    rvCollections.isVisible = state.collections.isNotEmpty()
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
                            // TODO: Show edit collection dialog
                            showMessage("Edit collection dialog (coming soon)")
                        }
                        is CollectionsListContract.SideEffect.ShowError -> {
                            showMessage(sideEffect.message)
                        }
                        is CollectionsListContract.SideEffect.ShowSuccess -> {
                            showMessage(sideEffect.message)
                        }
                    }
                }
            }
        }
    }

    private fun showMessage(message: String) {
        Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show()
    }

    private fun showCreateCollectionDialog() {
        val dialog = CreateCollectionDialog { name, description ->
            viewModel.onEvent(CollectionsListContract.Event.CreateCollection(name, description))
        }
        dialog.show(parentFragmentManager, "CreateCollectionDialog")
    }
}
