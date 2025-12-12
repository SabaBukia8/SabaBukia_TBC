package com.example.mtgcollectionmanager.presentation.screen.collection

import androidx.lifecycle.viewModelScope
import com.example.mtgcollectionmanager.domain.common.Resource
import com.example.mtgcollectionmanager.domain.usecase.auth.LogoutUseCase
import com.example.mtgcollectionmanager.domain.usecase.collection.GetCollectionCardsUseCase
import com.example.mtgcollectionmanager.presentation.common.BaseViewModel
import com.example.mtgcollectionmanager.presentation.mapper.toUi
import com.example.mtgcollectionmanager.presentation.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CollectionViewModel @Inject constructor(
    private val getCollectionCardsUseCase: GetCollectionCardsUseCase,
    private val removeCardFromCollectionUseCase: com.example.mtgcollectionmanager.domain.usecase.collection.RemoveCardFromCollectionUseCase,
    private val logoutUseCase: LogoutUseCase
) : BaseViewModel<CollectionContract.State, CollectionContract.Event, CollectionContract.SideEffect>(
    CollectionContract.State()
) {

    init {
        onEvent(CollectionContract.Event.LoadCollection)
    }

    override fun onEvent(event: CollectionContract.Event) {
        when (event) {
            is CollectionContract.Event.LoadCollection -> loadCollection()
            is CollectionContract.Event.RefreshCollection -> loadCollection()
            is CollectionContract.Event.ViewModeChanged -> {
                updateState { it.copy(viewMode = event.mode) }
            }
            is CollectionContract.Event.CardClicked -> {
                emitSideEffect(CollectionContract.SideEffect.NavigateToCardDetails(event.cardId))
            }
            is CollectionContract.Event.DeleteCardClicked -> {
                val card = state.value.allCards.find { it.cardId == event.cardId }
                if (card != null) {
                    emitSideEffect(CollectionContract.SideEffect.ShowDeleteConfirmation(card.cardId, card.name))
                }
            }
            is CollectionContract.Event.SearchClicked -> {
                emitSideEffect(CollectionContract.SideEffect.NavigateToSearch)
            }
            is CollectionContract.Event.LogoutClicked -> logout()
            is CollectionContract.Event.ColorFilterClicked -> {
                emitSideEffect(CollectionContract.SideEffect.ShowColorFilterDialog(state.value.colorFilters))
            }
            is CollectionContract.Event.ColorFilterApplied -> {
                updateState { it.copy(colorFilters = event.colorFilters, viewMode = CollectionContract.ViewMode.BY_COLOR) }
                applyFilters()
            }
            is CollectionContract.Event.SetFilterClicked -> {
                emitSideEffect(CollectionContract.SideEffect.ShowSetFilterDialog(state.value.setFilters))
            }
            is CollectionContract.Event.SetFilterApplied -> {
                updateState { it.copy(setFilters = event.setFilters, viewMode = CollectionContract.ViewMode.BY_SET) }
                applyFilters()
            }
        }
    }

    private fun loadCollection() {
        viewModelScope.launch {
            getCollectionCardsUseCase().collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        updateState { it.copy(isLoading = resource.isLoading) }
                    }
                    is Resource.Success -> {
                        resource.data?.let { collectionCards ->
                            val uiCards = collectionCards.map { it.toUi() }
                            val totalValue = collectionCards.sumOf { it.card.price * it.quantity }
                            val totalCards = collectionCards.sumOf { it.quantity }

                            updateState {
                                it.copy(
                                    allCards = uiCards,
                                    cards = uiCards,
                                    totalValue = String.format("$%.2f", totalValue),
                                    totalCards = totalCards
                                )
                            }
                            applyFilters()
                        }
                    }
                    is Resource.Error -> {
                        emitSideEffect(CollectionContract.SideEffect.ShowError(
                            UiText.DynamicString(resource.errorMessage)
                        ))
                    }
                }
            }
        }
    }

    private fun applyFilters() {
        val currentState = state.value
        val filteredCards = when (currentState.viewMode) {
            CollectionContract.ViewMode.ALL -> currentState.allCards
            CollectionContract.ViewMode.BY_COLOR -> {
                val includeColors = currentState.colorFilters
                    .filter { it.value == CollectionContract.FilterState.INCLUDE }
                    .keys
                val excludeColors = currentState.colorFilters
                    .filter { it.value == CollectionContract.FilterState.EXCLUDE }
                    .keys

                currentState.allCards.filter { card ->
                    // First check excludes - if card has any excluded color, filter it out
                    val hasExcludedColor = excludeColors.any { color -> card.colors.contains(color) }
                    if (hasExcludedColor) return@filter false

                    // Then check includes - if there are includes, card must have at least one
                    if (includeColors.isEmpty()) {
                        true // No includes means show all (that aren't excluded)
                    } else {
                        includeColors.any { color -> card.colors.contains(color) }
                    }
                }
            }
            CollectionContract.ViewMode.BY_SET -> {
                val includeSets = currentState.setFilters
                    .filter { it.value == CollectionContract.FilterState.INCLUDE }
                    .keys
                val excludeSets = currentState.setFilters
                    .filter { it.value == CollectionContract.FilterState.EXCLUDE }
                    .keys

                currentState.allCards.filter { card ->
                    // Check excludes first
                    if (excludeSets.contains(card.setCode)) return@filter false

                    // Then check includes
                    if (includeSets.isEmpty()) {
                        true // No includes means show all (that aren't excluded)
                    } else {
                        includeSets.contains(card.setCode)
                    }
                }
            }
        }

        updateState { it.copy(cards = filteredCards) }
    }

    fun deleteCard(cardId: String) {
        viewModelScope.launch {
            removeCardFromCollectionUseCase(cardId).collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        updateState { it.copy(isLoading = resource.isLoading) }
                    }
                    is Resource.Success -> {
                        emitSideEffect(CollectionContract.SideEffect.ShowSuccess(
                            UiText.StringResource(com.example.mtgcollectionmanager.R.string.card_removed_success)
                        ))
                    }
                    is Resource.Error -> {
                        emitSideEffect(CollectionContract.SideEffect.ShowError(
                            UiText.DynamicString(resource.errorMessage)
                        ))
                    }
                }
            }
        }
    }

    private fun logout() {
        viewModelScope.launch {
            logoutUseCase()
            emitSideEffect(CollectionContract.SideEffect.NavigateToLogin)
        }
    }
}
