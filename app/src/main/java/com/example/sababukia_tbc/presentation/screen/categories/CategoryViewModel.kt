package com.example.sababukia_tbc.presentation.screen.categories

import androidx.lifecycle.viewModelScope
import com.example.sababukia_tbc.domain.model.Category
import com.example.sababukia_tbc.domain.usecase.GetCategoriesUseCase
import com.example.sababukia_tbc.domain.usecase.SearchCategoriesUseCase
import com.example.sababukia_tbc.presentation.common.BaseViewModel
import com.example.sababukia_tbc.presentation.mapper.toPresentation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val searchCategoriesUseCase: SearchCategoriesUseCase
) : BaseViewModel<CategoryContract.State, CategoryContract.SideEffect, CategoryContract.Event>(
    CategoryContract.State()
) {

    private val searchQueryFlow = MutableStateFlow("")
    private var allCategories: List<Category> = emptyList()

    init {
        loadCategories()
    }

    private fun loadCategories() {
        handleResponse(
            apiCall = { getCategoriesUseCase() },
            onLoading = { setState { copy(isLoading = true) } },
            onSuccess = { categories ->
                allCategories = categories
                val mapped = categories.map { it.toPresentation() }
                setState {
                    copy(
                        categories = mapped,
                        filteredCategories = mapped,
                        isLoading = false
                    )
                }
                setupSearch()
            },
            onError = { message ->
                sendSideEffect(CategoryContract.SideEffect.ShowError(message))
                setState { copy(isLoading = false) }
            }
        )
    }

    private fun setupSearch() {
        searchCategoriesUseCase(
            queryFlow = searchQueryFlow,
            categories = allCategories
        ).onEach { filtered ->
            setState {
                copy(filteredCategories = filtered.map { it.toPresentation() })
            }
        }.launchIn(viewModelScope)
    }

    fun onEvent(event: CategoryContract.Event) {
        when (event) {
            is CategoryContract.Event.SearchQueryChanged -> {
                setState { copy(searchQuery = event.query) }
                searchQueryFlow.value = event.query
            }
        }
    }
}
