package com.example.mtgcollectionmanager.presentation.screen.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.mtgcollectionmanager.R
import com.example.mtgcollectionmanager.data.remote.util.NetworkConnectivityManager
import com.example.mtgcollectionmanager.domain.common.Resource
import com.example.mtgcollectionmanager.domain.usecase.card.GetCardDetailsUseCase
import com.example.mtgcollectionmanager.domain.usecase.collection.AddCardToCollectionUseCase
import com.example.mtgcollectionmanager.domain.usecase.collection.IsCardInCollectionUseCase
import com.example.mtgcollectionmanager.presentation.common.BaseViewModel
import com.example.mtgcollectionmanager.presentation.mapper.toUi
import com.example.mtgcollectionmanager.presentation.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CardDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getCardDetailsUseCase: GetCardDetailsUseCase,
    private val addCardToCollectionUseCase: AddCardToCollectionUseCase,
    private val isCardInCollectionUseCase: IsCardInCollectionUseCase,
    private val networkConnectivityManager: NetworkConnectivityManager
) : BaseViewModel<CardDetailsContract.State, CardDetailsContract.Event, CardDetailsContract.SideEffect>(
    CardDetailsContract.State(isNetworkAvailable = networkConnectivityManager.isNetworkAvailable())
) {

    private val collectionId: Long = savedStateHandle.get<Long>("collectionId") ?: 1L
    private var domainCard: com.example.mtgcollectionmanager.domain.model.Card? = null
    
    init {
        observeNetworkStatus()
    }
    
    private fun observeNetworkStatus() {
        networkConnectivityManager.observeNetworkState()
            .onEach { networkState -> 
                val isAvailable = networkState is NetworkConnectivityManager.NetworkState.Available
                updateState { it.copy(isNetworkAvailable = isAvailable) }
                

                if (isAvailable && domainCard != null) {
                    loadCard(domainCard!!.id)
                }
            }
            .launchIn(viewModelScope)
    }

    override fun onEvent(event: CardDetailsContract.Event) {
        when (event) {
            is CardDetailsContract.Event.LoadCard -> loadCard(event.cardId)
            is CardDetailsContract.Event.QuantityChanged -> {
                updateState { it.copy(quantity = event.quantity) }
            }
            is CardDetailsContract.Event.ConditionSelected -> {
                updateState { it.copy(selectedCondition = event.condition) }
            }
            is CardDetailsContract.Event.NotesChanged -> {
                updateState { it.copy(notes = event.notes) }
            }
            is CardDetailsContract.Event.AddToCollectionClicked -> addToCollection()
            is CardDetailsContract.Event.OpenMarketUrl -> {
                emitSideEffect(CardDetailsContract.SideEffect.OpenBrowser(event.url))
            }
        }
    }

    private fun loadCard(cardId: String) {
        viewModelScope.launch {
            getCardDetailsUseCase(cardId).collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        updateState { it.copy(isLoading = resource.isLoading) }
                    }
                    is Resource.Success -> {
                        resource.data?.let { card ->
                            domainCard = card
                            val isInCollection = isCardInCollectionUseCase(collectionId, cardId)
                            updateState {
                                it.copy(
                                    card = card.toUi(),
                                    isInCollection = isInCollection
                                )
                            }
                        }
                    }
                    is Resource.Error -> {

                        if (state.value.isNetworkAvailable) {
                            emitSideEffect(CardDetailsContract.SideEffect.ShowError(
                                UiText.DynamicString(resource.errorMessage)
                            ))
                        }
                    }
                }
            }
        }
    }

    private fun addToCollection() {
        viewModelScope.launch {
            with(state.value) {
                domainCard?.let { card ->
                    addCardToCollectionUseCase(
                        collectionId = collectionId,
                        card = card,
                        quantity = quantity,
                        condition = selectedCondition,
                        notes = notes
                    ).collect { resource ->
                        when (resource) {
                            is Resource.Loading -> {
                                updateState { it.copy(isLoading = resource.isLoading) }
                            }
                            is Resource.Success -> {
                                emitSideEffect(CardDetailsContract.SideEffect.ShowAddedToCollection)
                                emitSideEffect(CardDetailsContract.SideEffect.NavigateBack)
                            }
                            is Resource.Error -> {

                        if (state.value.isNetworkAvailable) {
                            emitSideEffect(CardDetailsContract.SideEffect.ShowError(
                                UiText.DynamicString(resource.errorMessage)
                            ))
                        }
                    }
                        }
                    }
                }
            }
        }
    }
}
