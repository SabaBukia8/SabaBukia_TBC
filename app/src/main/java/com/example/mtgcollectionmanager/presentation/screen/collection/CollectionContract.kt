package com.example.mtgcollectionmanager.presentation.screen.collection

import com.example.mtgcollectionmanager.presentation.model.CollectionCardUiModel
import com.example.mtgcollectionmanager.presentation.util.UiText

object CollectionContract {
    data class State(
        val isLoading: Boolean = true,
        val cards: List<CollectionCardUiModel> = emptyList(),
        val allCards: List<CollectionCardUiModel> = emptyList(),
        val viewMode: ViewMode = ViewMode.ALL,
        val totalValue: String = "$0.00",
        val totalCards: Int = 0,
        val colorFilters: Map<String, FilterState> = emptyMap(),
        val setFilters: Map<String, FilterState> = emptyMap()
    )

    enum class ViewMode {
        ALL, BY_COLOR, BY_SET
    }

    enum class FilterState {
        NEUTRAL,  // Not selected
        INCLUDE,  // Green checkmark - show only these
        EXCLUDE   // Red X - hide these
    }

    sealed interface Event {
        data object LoadCollection : Event
        data object RefreshCollection : Event
        data class ViewModeChanged(val mode: ViewMode) : Event
        data class CardClicked(val cardId: String) : Event
        data class DeleteCardClicked(val cardId: String) : Event
        data object SearchClicked : Event
        data object LogoutClicked : Event
        data object ColorFilterClicked : Event
        data class ColorFilterApplied(val colorFilters: Map<String, FilterState>) : Event
        data object SetFilterClicked : Event
        data class SetFilterApplied(val setFilters: Map<String, FilterState>) : Event
    }

    sealed interface SideEffect {
        data class NavigateToCardDetails(val cardId: String) : SideEffect
        data object NavigateToSearch : SideEffect
        data object NavigateToLogin : SideEffect
        data class ShowError(val message: UiText) : SideEffect
        data class ShowColorFilterDialog(val colorFilters: Map<String, FilterState>) : SideEffect
        data class ShowSetFilterDialog(val setFilters: Map<String, FilterState>) : SideEffect
        data class ShowDeleteConfirmation(val cardId: String, val cardName: String) : SideEffect
        data class ShowSuccess(val message: UiText) : SideEffect
    }
}
