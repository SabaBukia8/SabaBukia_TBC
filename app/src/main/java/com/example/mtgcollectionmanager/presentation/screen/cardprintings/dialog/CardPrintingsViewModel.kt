package com.example.mtgcollectionmanager.presentation.screen.cardprintings.dialog

import androidx.lifecycle.viewModelScope
import com.example.mtgcollectionmanager.data.remote.util.NetworkConnectivityManager
import com.example.mtgcollectionmanager.domain.common.Resource
import com.example.mtgcollectionmanager.domain.usecase.card.GetCardPrintingsUseCase
import com.example.mtgcollectionmanager.presentation.common.BaseViewModel
import com.example.mtgcollectionmanager.presentation.mapper.toUi
import com.example.mtgcollectionmanager.presentation.model.CardUiModel
import com.example.mtgcollectionmanager.presentation.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

object CardPrintingsContract {
    data class State(
        val isLoading: Boolean = false,
        val isNetworkAvailable: Boolean = true,
        val printings: List<CardUiModel> = emptyList(),
        val error: String? = null
    )

    sealed interface Event {
        data class LoadPrintings(val cardName: String) : Event
    }

    sealed interface SideEffect {
        data class ShowError(val message: UiText) : SideEffect
    }
}

@HiltViewModel
class CardPrintingsViewModel @Inject constructor(
    private val getCardPrintingsUseCase: GetCardPrintingsUseCase,
    private val networkConnectivityManager: NetworkConnectivityManager
) : BaseViewModel<CardPrintingsContract.State, CardPrintingsContract.Event, CardPrintingsContract.SideEffect>(
    CardPrintingsContract.State(isNetworkAvailable = networkConnectivityManager.isNetworkAvailable())
) {
    
    init {
        observeNetworkStatus()
    }
    
    private fun observeNetworkStatus() {
        networkConnectivityManager.observeNetworkState()
            .onEach { networkState -> 
                val isAvailable = networkState is NetworkConnectivityManager.NetworkState.Available
                updateState { it.copy(isNetworkAvailable = isAvailable) }
            }
            .launchIn(viewModelScope)
    }

    override fun onEvent(event: CardPrintingsContract.Event) {
        when (event) {
            is CardPrintingsContract.Event.LoadPrintings -> loadPrintings(event.cardName)
        }
    }

    fun loadPrintings(cardName: String) {
        viewModelScope.launch {
            getCardPrintingsUseCase(cardName).collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        updateState { it.copy(isLoading = resource.isLoading, error = null) }
                    }
                    is Resource.Success -> {
                        resource.data?.let { cards ->
                            updateState {
                                it.copy(
                                    printings = cards.map { card -> card.toUi() },
                                    isLoading = false,
                                    error = null
                                )
                            }
                        }
                    }
                    is Resource.Error -> {
                        updateState {
                            it.copy(
                                isLoading = false,
                                error = resource.errorMessage
                            )
                        }
                        

                        if (state.value.isNetworkAvailable) {
                            emitSideEffect(CardPrintingsContract.SideEffect.ShowError(
                                UiText.DynamicString(resource.errorMessage)
                            ))
                        }
                    }
                }
            }
        }
    }
}