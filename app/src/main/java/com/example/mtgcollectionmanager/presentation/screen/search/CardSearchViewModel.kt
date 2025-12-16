package com.example.mtgcollectionmanager.presentation.screen.search

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.mtgcollectionmanager.R
import com.example.mtgcollectionmanager.data.remote.util.NetworkConnectivityManager
import com.example.mtgcollectionmanager.domain.common.Resource
import com.example.mtgcollectionmanager.domain.usecase.card.SearchCardsUseCase
import com.example.mtgcollectionmanager.presentation.common.BaseViewModel
import com.example.mtgcollectionmanager.presentation.mapper.toUi
import com.example.mtgcollectionmanager.presentation.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CardSearchViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val searchCardsUseCase: SearchCardsUseCase,
    private val networkConnectivityManager: NetworkConnectivityManager
) : BaseViewModel<CardSearchContract.State, CardSearchContract.Event, CardSearchContract.SideEffect>(
    CardSearchContract.State(isNetworkAvailable = networkConnectivityManager.isNetworkAvailable())
) {

    val collectionId: Long = savedStateHandle.get<Long>("collectionId") ?: 1L

    init {
        observeNetworkStatus()
    }
    
    private fun observeNetworkStatus() {
        networkConnectivityManager.observeNetworkState()
            .onEach { networkState -> 
                val isAvailable = networkState is NetworkConnectivityManager.NetworkState.Available
                updateState { it.copy(isNetworkAvailable = isAvailable) }
                
                // If network becomes available and we have an active search query, refresh results
                if (isAvailable && state.value.searchQuery.isNotEmpty() && state.value.hasSearched) {
                    searchCards()
                }
            }
            .launchIn(viewModelScope)
    }

    override fun onEvent(event: CardSearchContract.Event) {
        when (event) {
            is CardSearchContract.Event.SearchQueryChanged -> {
                updateState { it.copy(searchQuery = event.query) }
            }
            is CardSearchContract.Event.SearchClicked -> searchCards()
            is CardSearchContract.Event.CardClicked -> {
                emitSideEffect(CardSearchContract.SideEffect.NavigateToCardDetails(event.cardId))
            }
            is CardSearchContract.Event.FiltersClicked -> {
                emitSideEffect(CardSearchContract.SideEffect.ShowFiltersDialog(state.value.filters))
            }
            is CardSearchContract.Event.FiltersApplied -> {
                updateState { it.copy(filters = event.filters) }
            }
        }
    }

    private fun searchCards() {
        viewModelScope.launch {
            with(state.value) {
                if (searchQuery.isBlank() && filters.isEmpty()) {
                    emitSideEffect(CardSearchContract.SideEffect.ShowError(
                        UiText.StringResource(R.string.search_empty_state)
                    ))
                    return@launch
                }

                // Build the complete query
                val fullQuery = buildSearchQuery(searchQuery, filters)

                searchCardsUseCase(fullQuery).collect { resource ->
                    when (resource) {
                        is Resource.Loading -> {
                            updateState { it.copy(isLoading = resource.isLoading) }
                        }
                        is Resource.Success -> {
                            resource.data?.let { cards ->
                                updateState {
                                    it.copy(
                                        cards = cards.map { card -> card.toUi() },
                                        hasSearched = true
                                    )
                                }
                            }
                        }
                        is Resource.Error -> {
                            updateState { it.copy(hasSearched = true) }
                            // Only show error messages if we're online - avoid showing network errors when offline
                            if (state.value.isNetworkAvailable) {
                                emitSideEffect(CardSearchContract.SideEffect.ShowError(
                                    UiText.DynamicString(resource.errorMessage)
                                ))
                            }
                        }
                    }
                }
            }
        }
    }

    private fun buildSearchQuery(nameQuery: String, filters: com.example.mtgcollectionmanager.presentation.screen.search.model.SearchFilters): String {
        val queryParts = mutableListOf<String>()

        // Add name query if present
        if (nameQuery.isNotBlank()) {
            queryParts.add("name:\"$nameQuery\"")
        }

        // Add filter query
        val filterQuery = filters.toScryfallQuery()
        if (filterQuery.isNotBlank()) {
            queryParts.add(filterQuery)
        }

        return queryParts.joinToString(" ")
    }
}
