package com.example.mtgcollectionmanager.presentation.screen.search

import com.example.mtgcollectionmanager.presentation.model.CardUiModel
import com.example.mtgcollectionmanager.presentation.screen.search.model.SearchFilters
import com.example.mtgcollectionmanager.presentation.util.UiText

object CardSearchContract {
    data class State(
        val isLoading: Boolean = false,
        val isNetworkAvailable: Boolean = true,
        val searchQuery: String = "",
        val cards: List<CardUiModel> = emptyList(),
        val hasSearched: Boolean = false,
        val filters: SearchFilters = SearchFilters()
    )

    sealed interface Event {
        data class SearchQueryChanged(val query: String) : Event
        data object SearchClicked : Event
        data class CardClicked(val cardId: String) : Event
        data object FiltersClicked : Event
        data class FiltersApplied(val filters: SearchFilters) : Event
    }

    sealed interface SideEffect {
        data class NavigateToCardDetails(val cardId: String) : SideEffect
        data class ShowError(val message: UiText) : SideEffect
        data class ShowFiltersDialog(val currentFilters: SearchFilters) : SideEffect
    }
}
