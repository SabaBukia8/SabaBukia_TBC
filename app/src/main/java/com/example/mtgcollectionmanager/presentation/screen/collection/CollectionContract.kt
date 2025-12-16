package com.example.mtgcollectionmanager.presentation.screen.collection

import com.example.mtgcollectionmanager.presentation.model.CollectionCardUiModel
import com.example.mtgcollectionmanager.presentation.screen.collection.drawer.CategoryDrawerItem
import com.example.mtgcollectionmanager.presentation.util.UiText

object CollectionContract {
    data class State(
        val isLoading: Boolean = true,
        val isNetworkAvailable: Boolean = true,
        val cards: List<CollectionCardUiModel> = emptyList(),
        val allCards: List<CollectionCardUiModel> = emptyList(),
        val viewMode: ViewMode = ViewMode.ALL,
        val totalValue: String = "$0.00",
        val totalCards: Int = 0,
        val colorFilters: Map<String, FilterState> = emptyMap(),
        val setFilters: Map<String, FilterState> = emptyMap(),
        val selectedCategoryId: Long? = null,
        val categories: List<CategoryDrawerItem> = emptyList(),
        val collections: List<com.example.mtgcollectionmanager.presentation.model.CollectionUi> = emptyList()
    )

    enum class ViewMode {
        ALL, BY_COLOR, BY_SET, BY_CATEGORY
    }

    enum class FilterState {
        NEUTRAL,  // Not selected
        INCLUDE,  // Green checkmark - show only these
        EXCLUDE   // Red X - hide these
    }

    sealed interface Event {
        data object LoadCollection : Event
        data object RefreshCollection : Event
        data object LoadCategories : Event
        data class ViewModeChanged(val mode: ViewMode) : Event
        data class CardClicked(val cardId: String) : Event
        data class DeleteCardClicked(val cardId: String) : Event
        data class EditCardClicked(val cardId: String) : Event
        data class SaveCardDetails(val cardId: String, val quantity: Int, val condition: String, val notes: String, val categoryId: Long?) : Event
        data object SearchClicked : Event
        data object LogoutClicked : Event
        data object ColorFilterClicked : Event
        data class ColorFilterApplied(val colorFilters: Map<String, FilterState>) : Event
        data object SetFilterClicked : Event
        data class SetFilterApplied(val setFilters: Map<String, FilterState>) : Event
        data class CategoryFilterClicked(val categoryId: Long?) : Event
        data object ManageCollectionsClicked : Event
        data object LoadCollections : Event
        data class CreateCollection(val name: String, val description: String) : Event
        data class UpdateCollection(val id: Long, val name: String, val description: String) : Event
        data class DeleteCollection(val id: Long) : Event
        data object ManageCategoriesClicked : Event
        data class CreateCategory(val name: String, val color: String) : Event
        data class UpdateCategory(val id: Long, val name: String, val color: String) : Event
        data class DeleteCategory(val id: Long) : Event
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
        data class ShowEditCardDialog(val card: CollectionCardUiModel) : SideEffect
        data class ShowManageCollectionsDialog(val collections: List<com.example.mtgcollectionmanager.presentation.model.CollectionUi>) : SideEffect
        data class ShowManageCategoriesDialog(val categories: List<com.example.mtgcollectionmanager.domain.model.Category>) : SideEffect
    }
}
