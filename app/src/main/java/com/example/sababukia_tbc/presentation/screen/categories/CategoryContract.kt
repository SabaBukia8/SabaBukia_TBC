package com.example.sababukia_tbc.presentation.screen.categories

import com.example.sababukia_tbc.presentation.model.CategoryModel

object CategoryContract {

    data class State(
        val searchQuery: String = "",
        val categories: List<CategoryModel> = emptyList(),
        val filteredCategories: List<CategoryModel> = emptyList(),
        val isLoading: Boolean = false
    )

    sealed interface SideEffect {
        data class ShowError(val message: String) : SideEffect
    }

    sealed interface Event {
        data class SearchQueryChanged(val query: String) : Event
    }
}
