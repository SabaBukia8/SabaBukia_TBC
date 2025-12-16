package com.example.mtgcollectionmanager.presentation.screen.collectionslist

import com.example.mtgcollectionmanager.presentation.model.CollectionUi
import com.example.mtgcollectionmanager.presentation.util.UiText

object CollectionsListContract {
    data class State(
        val collections: List<CollectionUi> = emptyList(),
        val isLoading: Boolean = false,
        val error: String? = null,
        val isNetworkAvailable: Boolean = true
    )

    sealed interface Event {
        object LoadCollections : Event
        data class OnCollectionClick(val collectionId: Long) : Event
        object OnCreateCollectionClick : Event
        data class CreateCollection(val name: String, val description: String) : Event
        data class OnDeleteCollectionClick(val collectionId: Long) : Event
        data class OnEditCollectionClick(val collectionId: Long) : Event
        data class UpdateCollection(val collectionId: Long, val name: String, val description: String) : Event
    }

    sealed interface SideEffect {
        data class NavigateToCollection(val collectionId: Long) : SideEffect
        object ShowCreateCollectionDialog : SideEffect
        data class ShowEditCollectionDialog(val collectionId: Long) : SideEffect
        data class ShowError(val message: UiText) : SideEffect
        data class ShowSuccess(val message: UiText) : SideEffect
    }
}
