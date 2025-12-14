package com.example.mtgcollectionmanager.presentation.screen.collection

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.mtgcollectionmanager.domain.common.Resource
import com.example.mtgcollectionmanager.domain.model.CardCondition
import com.example.mtgcollectionmanager.domain.usecase.auth.LogoutUseCase
import com.example.mtgcollectionmanager.domain.usecase.collection.GetCollectionCardsUseCase
import com.example.mtgcollectionmanager.presentation.common.BaseViewModel
import com.example.mtgcollectionmanager.presentation.mapper.toUi
import com.example.mtgcollectionmanager.presentation.screen.collection.drawer.CategoryDrawerItem
import com.example.mtgcollectionmanager.presentation.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CollectionViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getCollectionCardsUseCase: GetCollectionCardsUseCase,
    private val getCardsByCategoryUseCase: com.example.mtgcollectionmanager.domain.usecase.collection.GetCardsByCategoryUseCase,
    private val getCategoriesUseCase: com.example.mtgcollectionmanager.domain.usecase.collection.GetCategoriesUseCase,
    private val updateCardDetailsUseCase: com.example.mtgcollectionmanager.domain.usecase.collection.UpdateCardDetailsUseCase,
    private val removeCardFromCollectionUseCase: com.example.mtgcollectionmanager.domain.usecase.collection.RemoveCardFromCollectionUseCase,
    private val getUserCollectionsUseCase: com.example.mtgcollectionmanager.domain.usecase.collection.GetUserCollectionsUseCase,
    private val createCollectionUseCase: com.example.mtgcollectionmanager.domain.usecase.collection.CreateCollectionUseCase,
    private val updateCollectionUseCase: com.example.mtgcollectionmanager.domain.usecase.collection.UpdateCollectionUseCase,
    private val deleteCollectionUseCase: com.example.mtgcollectionmanager.domain.usecase.collection.DeleteCollectionUseCase,
    private val createCategoryUseCase: com.example.mtgcollectionmanager.domain.usecase.collection.CreateCategoryUseCase,
    private val updateCategoryUseCase: com.example.mtgcollectionmanager.domain.usecase.collection.UpdateCategoryUseCase,
    private val deleteCategoryUseCase: com.example.mtgcollectionmanager.domain.usecase.collection.DeleteCategoryUseCase,
    private val moveCardToCategoryUseCase: com.example.mtgcollectionmanager.domain.usecase.collection.MoveCardToCategoryUseCase,
    private val logoutUseCase: LogoutUseCase
) : BaseViewModel<CollectionContract.State, CollectionContract.Event, CollectionContract.SideEffect>(
    CollectionContract.State()
) {

    val collectionId: Long = savedStateHandle.get<Long>("collectionId") ?: 1L

    init {
        onEvent(CollectionContract.Event.LoadCollection)
        onEvent(CollectionContract.Event.LoadCategories)
    }

    override fun onEvent(event: CollectionContract.Event) {
        when (event) {
            is CollectionContract.Event.LoadCollection -> loadCollection()
            is CollectionContract.Event.RefreshCollection -> loadCollection()
            is CollectionContract.Event.LoadCategories -> loadCategories()
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
            is CollectionContract.Event.CategoryFilterClicked -> {
                when {
                    event.categoryId == null -> {
                        // All cards
                        updateState {
                            it.copy(
                                selectedCategoryId = null,
                                viewMode = CollectionContract.ViewMode.ALL
                            )
                        }
                        loadCollection()
                    }
                    event.categoryId == -1L -> {
                        // Uncategorized cards
                        updateState {
                            it.copy(
                                selectedCategoryId = -1L,
                                viewMode = CollectionContract.ViewMode.BY_CATEGORY
                            )
                        }
                        loadUncategorizedCards()
                    }
                    else -> {
                        // Specific category
                        updateState {
                            it.copy(
                                selectedCategoryId = event.categoryId,
                                viewMode = CollectionContract.ViewMode.BY_CATEGORY
                            )
                        }
                        loadCategoryCards(event.categoryId)
                    }
                }
            }
            is CollectionContract.Event.EditCardClicked -> {
                val card = state.value.allCards.find { it.cardId == event.cardId }
                if (card != null) {
                    emitSideEffect(CollectionContract.SideEffect.ShowEditCardDialog(card))
                }
            }
            is CollectionContract.Event.SaveCardDetails -> {
                saveCardDetails(event.cardId, event.quantity, event.condition, event.notes, event.categoryId)
            }
            is CollectionContract.Event.ManageCollectionsClicked -> {
                loadCollections()
            }
            is CollectionContract.Event.LoadCollections -> {
                loadCollections()
            }
            is CollectionContract.Event.CreateCollection -> {
                createCollection(event.name, event.description)
            }
            is CollectionContract.Event.UpdateCollection -> {
                updateCollection(event.id, event.name, event.description)
            }
            is CollectionContract.Event.DeleteCollection -> {
                deleteCollection(event.id)
            }
            is CollectionContract.Event.ManageCategoriesClicked -> {
                loadCategoriesForManagement()
            }
            is CollectionContract.Event.CreateCategory -> {
                createCategory(event.name, event.color)
            }
            is CollectionContract.Event.UpdateCategory -> {
                updateCategory(event.id, event.name, event.color)
            }
            is CollectionContract.Event.DeleteCategory -> {
                deleteCategory(event.id)
            }
        }
    }

    private fun loadCollection() {
        viewModelScope.launch {
            getCollectionCardsUseCase(collectionId).collect { resource ->
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
            CollectionContract.ViewMode.BY_CATEGORY -> currentState.allCards // Already filtered by repository
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
            removeCardFromCollectionUseCase(collectionId, cardId).collect { resource ->
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

    private fun loadCategories() {
        viewModelScope.launch {
            getCategoriesUseCase(collectionId).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        resource.data?.let { categories ->
                            val drawerItems = buildList {
                                add(CategoryDrawerItem.AllCards)
                                add(CategoryDrawerItem.Uncategorized)
                                categories.forEach { category ->
                                    add(
                                        CategoryDrawerItem.Category(
                                            id = category.id,
                                            name = category.name,
                                            color = category.color,
                                            cardCount = category.cardCount
                                        )
                                    )
                                }
                            }
                            updateState { it.copy(categories = drawerItems) }
                        }
                    }
                    is Resource.Error -> {
                        // Silently fail for categories loading
                    }
                    is Resource.Loading -> {
                        // No need to show loading for categories
                    }
                }
            }
        }
    }

    private fun loadCategoryCards(categoryId: Long) {
        viewModelScope.launch {
            getCardsByCategoryUseCase(collectionId, categoryId).collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        updateState { it.copy(isLoading = resource.isLoading) }
                    }
                    is Resource.Success -> {
                        resource.data?.let { collectionCards ->
                            val uiCards = collectionCards.map { it.toUi() }
                            updateState {
                                it.copy(
                                    cards = uiCards,
                                    allCards = uiCards
                                )
                            }
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

    private fun loadUncategorizedCards() {
        viewModelScope.launch {
            getCardsByCategoryUseCase(collectionId, null).collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        updateState { it.copy(isLoading = resource.isLoading) }
                    }
                    is Resource.Success -> {
                        resource.data?.let { collectionCards ->
                            val uiCards = collectionCards.map { it.toUi() }
                            updateState {
                                it.copy(
                                    cards = uiCards,
                                    allCards = uiCards
                                )
                            }
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

    private fun saveCardDetails(cardId: String, quantity: Int, conditionStr: String, notes: String, categoryId: Long?) {
        viewModelScope.launch {
            val condition = try {
                CardCondition.valueOf(conditionStr)
            } catch (e: Exception) {
                CardCondition.NEAR_MINT
            }

            // Update card details (quantity, condition, notes)
            updateCardDetailsUseCase(collectionId, cardId, quantity, condition, notes).collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        updateState { it.copy(isLoading = resource.isLoading) }
                    }
                    is Resource.Success -> {
                        // After updating details, move card to category if needed
                        if (categoryId != null) {
                            moveCardToCategory(cardId, categoryId)
                        } else {
                            emitSideEffect(CollectionContract.SideEffect.ShowSuccess(
                                UiText.StringResource(com.example.mtgcollectionmanager.R.string.card_updated_success)
                            ))
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

    private fun moveCardToCategory(cardId: String, categoryId: Long) {
        viewModelScope.launch {
            moveCardToCategoryUseCase(collectionId, cardId, categoryId).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        emitSideEffect(CollectionContract.SideEffect.ShowSuccess(
                            UiText.StringResource(com.example.mtgcollectionmanager.R.string.card_updated_success)
                        ))
                        loadCategories() // Refresh categories to update card counts
                    }
                    is Resource.Error -> {
                        emitSideEffect(CollectionContract.SideEffect.ShowError(
                            UiText.DynamicString(resource.errorMessage)
                        ))
                    }
                    is Resource.Loading -> {
                        // Already showing loading from update card details
                    }
                }
            }
        }
    }

    private fun loadCollections() {
        viewModelScope.launch {
            getUserCollectionsUseCase().collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        resource.data?.let { collections ->
                            val collectionsUi = collections.map { it.toUi() }
                            updateState { it.copy(collections = collectionsUi) }
                            emitSideEffect(CollectionContract.SideEffect.ShowManageCollectionsDialog(collectionsUi))
                        }
                    }
                    is Resource.Error -> {
                        emitSideEffect(CollectionContract.SideEffect.ShowError(
                            UiText.DynamicString(resource.errorMessage)
                        ))
                    }
                    is Resource.Loading -> {
                        // No need to show loading for collections
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
                        emitSideEffect(CollectionContract.SideEffect.ShowSuccess(
                            UiText.StringResource(com.example.mtgcollectionmanager.R.string.collection_created_success)
                        ))
                        loadCollections()
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

    private fun updateCollection(id: Long, name: String, description: String) {
        viewModelScope.launch {
            // Find the existing collection to get all required fields
            val existingCollection = state.value.collections.find { it.id == id }
            if (existingCollection == null) {
                emitSideEffect(CollectionContract.SideEffect.ShowError(
                    UiText.DynamicString("Collection not found")
                ))
                return@launch
            }

            // Create updated domain Collection object
            val updatedCollection = com.example.mtgcollectionmanager.domain.model.Collection(
                id = id,
                name = name,
                description = description,
                createdDate = System.currentTimeMillis(), // Will be ignored by update
                userId = "", // Will be filled by repository
                totalCards = existingCollection.totalCards,
                totalValue = 0.0 // Will be recalculated
            )

            updateCollectionUseCase(updatedCollection).collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        updateState { it.copy(isLoading = resource.isLoading) }
                    }
                    is Resource.Success -> {
                        emitSideEffect(CollectionContract.SideEffect.ShowSuccess(
                            UiText.StringResource(com.example.mtgcollectionmanager.R.string.collection_updated_success)
                        ))
                        loadCollections()
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

    private fun deleteCollection(id: Long) {
        viewModelScope.launch {
            deleteCollectionUseCase(id).collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        updateState { it.copy(isLoading = resource.isLoading) }
                    }
                    is Resource.Success -> {
                        emitSideEffect(CollectionContract.SideEffect.ShowSuccess(
                            UiText.StringResource(com.example.mtgcollectionmanager.R.string.collection_deleted_success)
                        ))
                        loadCollections()
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

    private fun loadCategoriesForManagement() {
        viewModelScope.launch {
            getCategoriesUseCase(collectionId).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        resource.data?.let { categories ->
                            emitSideEffect(CollectionContract.SideEffect.ShowManageCategoriesDialog(categories))
                        }
                    }
                    is Resource.Error -> {
                        emitSideEffect(CollectionContract.SideEffect.ShowError(
                            UiText.DynamicString(resource.errorMessage)
                        ))
                    }
                    is Resource.Loading -> {
                        // No need to show loading
                    }
                }
            }
        }
    }

    private fun createCategory(name: String, color: String) {
        viewModelScope.launch {
            createCategoryUseCase(collectionId, name, color).collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        updateState { it.copy(isLoading = resource.isLoading) }
                    }
                    is Resource.Success -> {
                        emitSideEffect(CollectionContract.SideEffect.ShowSuccess(
                            UiText.StringResource(com.example.mtgcollectionmanager.R.string.category_created_success)
                        ))
                        loadCategories()
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

    private fun updateCategory(id: Long, name: String, color: String) {
        viewModelScope.launch {
            // Create updated Category object with required fields
            val updatedCategory = com.example.mtgcollectionmanager.domain.model.Category(
                id = id,
                collectionId = collectionId,
                name = name,
                color = color,
                createdDate = System.currentTimeMillis(), // Will be ignored by update
                cardCount = 0 // Will be recalculated
            )

            updateCategoryUseCase(updatedCategory).collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        updateState { it.copy(isLoading = resource.isLoading) }
                    }
                    is Resource.Success -> {
                        emitSideEffect(CollectionContract.SideEffect.ShowSuccess(
                            UiText.StringResource(com.example.mtgcollectionmanager.R.string.category_updated_success)
                        ))
                        loadCategories()
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

    private fun deleteCategory(id: Long) {
        viewModelScope.launch {
            deleteCategoryUseCase(id).collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        updateState { it.copy(isLoading = resource.isLoading) }
                    }
                    is Resource.Success -> {
                        emitSideEffect(CollectionContract.SideEffect.ShowSuccess(
                            UiText.StringResource(com.example.mtgcollectionmanager.R.string.category_deleted_success)
                        ))
                        loadCategories()
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
