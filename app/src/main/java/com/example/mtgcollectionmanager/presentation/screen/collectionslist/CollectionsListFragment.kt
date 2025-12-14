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
    private lateinit var llEmptyState: View

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
        llEmptyState = view.findViewById(R.id.llEmptyState)

        rvCollections.adapter = adapter

        fabCreateCollection.setOnClickListener {
            viewModel.onEvent(CollectionsListContract.Event.OnCreateCollectionClick)
        }

        observeState()
        observeSideEffects()
    }

    override fun onResume() {
        super.onResume()
        // Refresh collections to show updated stats
        viewModel.onEvent(CollectionsListContract.Event.LoadCollections)
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    progressBar.isVisible = state.isLoading

                    adapter.submitList(state.collections)

                    llEmptyState.isVisible = state.collections.isEmpty() && !state.isLoading
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
                            showEditCollectionDialog(sideEffect.collectionId)
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

    private fun showEditCollectionDialog(collectionId: Long) {
        // Find the collection from state
        val collection = viewModel.state.value.collections.find { it.id == collectionId } ?: return

        val dialog = EditCollectionDialog(
            collectionId = collectionId,
            currentName = collection.name,
            currentDescription = collection.description
        ) { id, name, description ->
            viewModel.onEvent(CollectionsListContract.Event.UpdateCollection(id, name, description))
        }
        dialog.show(parentFragmentManager, "EditCollectionDialog")
    }
}
