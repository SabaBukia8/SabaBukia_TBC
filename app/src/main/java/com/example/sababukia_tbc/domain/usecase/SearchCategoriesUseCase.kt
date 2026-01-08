package com.example.sababukia_tbc.domain.usecase

import com.example.sababukia_tbc.domain.model.Category
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SearchCategoriesUseCase @Inject constructor() {

    companion object {
        private const val SEARCH_DEBOUNCE_MS = 300L
    }

    @OptIn(FlowPreview::class)
    operator fun invoke(
        queryFlow: Flow<String>,
        categories: List<Category>
    ): Flow<List<Category>> {
        return queryFlow
            .debounce(SEARCH_DEBOUNCE_MS)
            .map { query ->
                if (query.isBlank()) {
                    categories
                } else {
                    categories.filter { category ->
                        category.name.contains(query, ignoreCase = true) ||
                        category.nameDe?.contains(query, ignoreCase = true) == true
                    }
                }
            }
    }
}
