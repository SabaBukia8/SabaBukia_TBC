package com.example.mtgcollectionmanager.presentation.screen.collectionslist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mtgcollectionmanager.domain.common.Resource
import com.example.mtgcollectionmanager.domain.usecase.collection.CreateCollectionUseCase
import com.example.mtgcollectionmanager.domain.usecase.collection.DeleteCollectionUseCase
import com.example.mtgcollectionmanager.domain.usecase.collection.EnsureDefaultCollectionUseCase
import com.example.mtgcollectionmanager.domain.usecase.collection.GetUserCollectionsUseCase
import com.example.mtgcollectionmanager.presentation.mapper.toUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CollectionsListViewModel @Inject constructor(
    private val getUserCollectionsUseCase: GetUserCollectionsUseCase,
    private val createCollectionUseCase: CreateCollectionUseCase,
    private val deleteCollectionUseCase: DeleteCollectionUseCase,
    private val ensureDefaultCollectionUseCase: EnsureDefaultCollectionUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(CollectionsListContract.State())
    val state: StateFlow<CollectionsListContract.State> = _state.asStateFlow()

    private val _sideEffect = Channel<CollectionsListContract.SideEffect>()
    val sideEffect = _sideEffect.receiveAsFlow()

    init {
        ensureDefaultCollectionAndLoad()
    }

    private fun ensureDefaultCollectionAndLoad() {
        viewModelScope.launch {
            ensureDefaultCollectionUseCase().collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        loadCollections()
                    }
                    is Resource.Error -> {
                        // Still try to load collections even if default creation fails
                        loadCollections()
                    }
                    is Resource.Loading -> {
                        _state.update { it.copy(isLoading = resource.isLoading) }
                    }
                }
            }
        }
    }

    fun onEvent(event: CollectionsListContract.Event) {
        when (event) {
            is CollectionsListContract.Event.LoadCollections -> loadCollections()
            is CollectionsListContract.Event.OnCollectionClick -> {
                viewModelScope.launch {
                    _sideEffect.send(CollectionsListContract.SideEffect.NavigateToCollection(event.collectionId))
                }
            }
            is CollectionsListContract.Event.OnCreateCollectionClick -> {
                viewModelScope.launch {
                    _sideEffect.send(CollectionsListContract.SideEffect.ShowCreateCollectionDialog)
                }
            }
            is CollectionsListContract.Event.CreateCollection -> {
                createCollection(event.name, event.description)
            }
            is CollectionsListContract.Event.OnDeleteCollectionClick -> {
                deleteCollection(event.collectionId)
            }
            is CollectionsListContract.Event.OnEditCollectionClick -> {
                viewModelScope.launch {
                    _sideEffect.send(CollectionsListContract.SideEffect.ShowEditCollectionDialog(event.collectionId))
                }
            }
        }
    }

    private fun loadCollections() {
        viewModelScope.launch {
            getUserCollectionsUseCase().collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        _state.update { it.copy(isLoading = resource.isLoading) }
                    }
                    is Resource.Success -> {
                        _state.update {
                            it.copy(
                                collections = resource.data.map { collection -> collection.toUi() },
                                error = null
                            )
                        }
                    }
                    is Resource.Error -> {
                        _state.update { it.copy(error = resource.errorMessage) }
                        _sideEffect.send(CollectionsListContract.SideEffect.ShowError(resource.errorMessage))
                    }
                }
            }
        }
    }

    private fun createCollection(name: String, description: String) {
        viewModelScope.launch {
            createCollectionUseCase(name, description).collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        _state.update { it.copy(isLoading = resource.isLoading) }
                    }
                    is Resource.Success -> {
                        _sideEffect.send(CollectionsListContract.SideEffect.ShowSuccess("Collection created"))
                        loadCollections() // Reload collections
                    }
                    is Resource.Error -> {
                        _sideEffect.send(CollectionsListContract.SideEffect.ShowError(resource.errorMessage))
                    }
                }
            }
        }
    }

    private fun deleteCollection(collectionId: Long) {
        viewModelScope.launch {
            deleteCollectionUseCase(collectionId).collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        _state.update { it.copy(isLoading = resource.isLoading) }
                    }
                    is Resource.Success -> {
                        _sideEffect.send(CollectionsListContract.SideEffect.ShowSuccess("Collection deleted"))
                        loadCollections() // Reload collections
                    }
                    is Resource.Error -> {
                        _sideEffect.send(CollectionsListContract.SideEffect.ShowError(resource.errorMessage))
                    }
                }
            }
        }
    }
}
