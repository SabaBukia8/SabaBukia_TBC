package com.example.mtgcollectionmanager.presentation.screen.collectionslist

import androidx.lifecycle.viewModelScope
import com.example.mtgcollectionmanager.R
import com.example.mtgcollectionmanager.data.remote.util.NetworkConnectivityManager
import com.example.mtgcollectionmanager.data.remote.util.executeWithFallback
import com.example.mtgcollectionmanager.domain.common.Resource
import com.example.mtgcollectionmanager.domain.usecase.collection.CreateCollectionUseCase
import com.example.mtgcollectionmanager.domain.usecase.collection.DeleteCollectionUseCase
import com.example.mtgcollectionmanager.domain.usecase.collection.EnsureDefaultCollectionUseCase
import com.example.mtgcollectionmanager.domain.usecase.collection.GetUserCollectionsUseCase
import com.example.mtgcollectionmanager.domain.usecase.collection.UpdateCollectionUseCase
import com.example.mtgcollectionmanager.presentation.common.BaseViewModel
import com.example.mtgcollectionmanager.presentation.mapper.toDomain
import com.example.mtgcollectionmanager.presentation.mapper.toUi
import com.example.mtgcollectionmanager.presentation.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CollectionsListViewModel @Inject constructor(
    private val getUserCollectionsUseCase: GetUserCollectionsUseCase,
    private val createCollectionUseCase: CreateCollectionUseCase,
    private val updateCollectionUseCase: UpdateCollectionUseCase,
    private val deleteCollectionUseCase: DeleteCollectionUseCase,
    private val ensureDefaultCollectionUseCase: EnsureDefaultCollectionUseCase,
    private val networkConnectivityManager: NetworkConnectivityManager
) : BaseViewModel<CollectionsListContract.State, CollectionsListContract.Event, CollectionsListContract.SideEffect>(
    CollectionsListContract.State(isNetworkAvailable = networkConnectivityManager.isNetworkAvailable())
) {

    init {
        observeNetworkStatus()
        ensureDefaultCollectionAndLoad()
    }
    
    private fun observeNetworkStatus() {
        networkConnectivityManager.observeNetworkState()
            .onEach { networkState -> 
                val isAvailable = networkState is NetworkConnectivityManager.NetworkState.Available
                updateState { it.copy(isNetworkAvailable = isAvailable) }
                
                // If network becomes available, refresh the collections
                if (isAvailable) {
                    loadCollections()
                }
            }
            .launchIn(viewModelScope)
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
                        updateState { it.copy(isLoading = resource.isLoading) }
                    }
                }
            }
        }
    }

    override fun onEvent(event: CollectionsListContract.Event) {
        when (event) {
            is CollectionsListContract.Event.LoadCollections -> loadCollections()
            is CollectionsListContract.Event.OnCollectionClick -> {
                emitSideEffect(CollectionsListContract.SideEffect.NavigateToCollection(event.collectionId))
            }
            is CollectionsListContract.Event.OnCreateCollectionClick -> {
                emitSideEffect(CollectionsListContract.SideEffect.ShowCreateCollectionDialog)
            }
            is CollectionsListContract.Event.CreateCollection -> {
                createCollection(event.name, event.description)
            }
            is CollectionsListContract.Event.OnDeleteCollectionClick -> {
                deleteCollection(event.collectionId)
            }
            is CollectionsListContract.Event.OnEditCollectionClick -> {
                emitSideEffect(CollectionsListContract.SideEffect.ShowEditCollectionDialog(event.collectionId))
            }
            is CollectionsListContract.Event.UpdateCollection -> {
                updateCollection(event.collectionId, event.name, event.description)
            }
        }
    }

    private fun loadCollections() {
        viewModelScope.launch {
            // Use executeWithFallback to handle network state gracefully
            executeWithFallback(
                networkManager = networkConnectivityManager,
                networkOperation = { getUserCollectionsUseCase() },
                fallbackOperation = { 
                    // Return whatever collections we have in cache when offline
                    flow {
                        emit(Resource.Loading(true))
                        emit(Resource.Success(state.value.collections.map { it.toDomain() }))
                        emit(Resource.Loading(false))
                    }
                }
            ).collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        updateState { it.copy(isLoading = resource.isLoading) }
                    }
                    is Resource.Success -> {
                        updateState {
                            it.copy(
                                collections = resource.data.map { collection -> collection.toUi() },
                                error = null
                            )
                        }
                    }
                    is Resource.Error -> {
                        updateState { it.copy(error = resource.errorMessage) }
                        // Only show error if we're online - avoid showing network errors when offline
                        if (state.value.isNetworkAvailable) {
                            emitSideEffect(CollectionsListContract.SideEffect.ShowError(
                                UiText.DynamicString(resource.errorMessage)
                            ))
                        }
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
                        updateState { it.copy(isLoading = resource.isLoading) }
                    }
                    is Resource.Success -> {
                        emitSideEffect(CollectionsListContract.SideEffect.ShowSuccess(
                            UiText.StringResource(R.string.collection_created_success)
                        ))
                        loadCollections()
                    }
                    is Resource.Error -> {
                        emitSideEffect(CollectionsListContract.SideEffect.ShowError(
                            UiText.DynamicString(resource.errorMessage)
                        ))
                    }
                }
            }
        }
    }

    private fun updateCollection(collectionId: Long, name: String, description: String) {
        viewModelScope.launch {
            val collection = state.value.collections.find { it.id == collectionId } ?: return@launch

            val domainCollection = collection.copy(
                name = name,
                description = description
            ).toDomain()

            updateCollectionUseCase(domainCollection).collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        updateState { it.copy(isLoading = resource.isLoading) }
                    }
                    is Resource.Success -> {
                        emitSideEffect(CollectionsListContract.SideEffect.ShowSuccess(
                            UiText.StringResource(R.string.collection_updated_success)
                        ))
                        loadCollections()
                    }
                    is Resource.Error -> {
                        emitSideEffect(CollectionsListContract.SideEffect.ShowError(
                            UiText.DynamicString(resource.errorMessage)
                        ))
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
                        updateState { it.copy(isLoading = resource.isLoading) }
                    }
                    is Resource.Success -> {
                        emitSideEffect(CollectionsListContract.SideEffect.ShowSuccess(
                            UiText.StringResource(R.string.collection_deleted_success)
                        ))
                        loadCollections()
                    }
                    is Resource.Error -> {
                        emitSideEffect(CollectionsListContract.SideEffect.ShowError(
                            UiText.DynamicString(resource.errorMessage)
                        ))
                    }
                }
            }
        }
    }
}
