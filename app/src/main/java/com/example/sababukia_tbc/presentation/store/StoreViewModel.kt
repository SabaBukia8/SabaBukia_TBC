package com.example.sababukia_tbc.presentation.store

import androidx.lifecycle.viewModelScope
import com.example.sababukia_tbc.domain.model.Category
import com.example.sababukia_tbc.domain.model.Event
import com.example.sababukia_tbc.domain.model.Result
import com.example.sababukia_tbc.domain.model.StoreError
import com.example.sababukia_tbc.domain.repository.StoreRepository
import com.example.sababukia_tbc.presentation.common.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StoreViewModel @Inject constructor(
    private val storeRepository: StoreRepository
) : BaseViewModel<StoreState, StoreEvent, StoreSideEffect>(StoreState()) {

    init {
        loadData()
    }

    override fun onEvent(event: StoreEvent) {
        when (event) {
            is StoreEvent.CategorySelected -> selectCategory(event.category)
            StoreEvent.Retry -> loadData()
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            updateState { copy(isLoading = true, error = null) }

            val eventsResult = storeRepository.getEvents()
            val categoriesResult = storeRepository.getCategories()

            when {
                eventsResult is Result.Success && categoriesResult is Result.Success -> {
                    updateState {
                        copy(
                            events = eventsResult.data,
                            categories = categoriesResult.data,
                            isLoading = false
                        )
                    }
                }
                eventsResult is Result.Error -> {
                    updateState { copy(isLoading = false, error = mapError(eventsResult.error)) }
                }
                categoriesResult is Result.Error -> {
                    updateState { copy(isLoading = false, error = mapError(categoriesResult.error)) }
                }
            }
        }
    }

    private fun selectCategory(category: String) {
        updateState { copy(selectedCategory = category) }
    }

    private fun mapError(error: StoreError): String = when (error) {
        is StoreError.NetworkError -> "Network error. Please check your connection."
        is StoreError.Unknown -> "An unexpected error occurred."
    }
}

data class StoreState(
    val events: List<Event> = emptyList(),
    val categories: List<Category> = emptyList(),
    val selectedCategory: String = "all",
    val isLoading: Boolean = false,
    val error: String? = null
) {
    val filteredEvents: List<Event>
        get() = if (selectedCategory == "all") {
            events
        } else {
            events.filter { it.category.equals(selectedCategory, ignoreCase = true) }
        }
}

sealed class StoreEvent {
    data class CategorySelected(val category: String) : StoreEvent()
    data object Retry : StoreEvent()
}

sealed class StoreSideEffect
