package com.example.mtgcollectionmanager.presentation.screen.details

import com.example.mtgcollectionmanager.domain.model.CardCondition
import com.example.mtgcollectionmanager.presentation.model.CardUiModel
import com.example.mtgcollectionmanager.presentation.util.UiText

object CardDetailsContract {
    data class State(
        val isLoading: Boolean = true,
        val isNetworkAvailable: Boolean = true,
        val card: CardUiModel? = null,
        val quantity: Int = 1,
        val selectedCondition: CardCondition = CardCondition.NEAR_MINT,
        val notes: String = "",
        val isInCollection: Boolean = false
    )

    sealed interface Event {
        data class LoadCard(val cardId: String) : Event
        data class QuantityChanged(val quantity: Int) : Event
        data class ConditionSelected(val condition: CardCondition) : Event
        data class NotesChanged(val notes: String) : Event
        data object AddToCollectionClicked : Event
        data class OpenMarketUrl(val url: String) : Event
    }

    sealed interface SideEffect {
        data object NavigateBack : SideEffect
        data object ShowAddedToCollection : SideEffect
        data class ShowError(val message: UiText) : SideEffect
        data class OpenBrowser(val url: String) : SideEffect
    }
}
