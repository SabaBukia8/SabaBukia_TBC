package com.example.mtgcollectionmanager.presentation.screen.common.dialog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mtgcollectionmanager.domain.common.Resource
import com.example.mtgcollectionmanager.domain.usecase.card.GetCardPrintingsUseCase
import com.example.mtgcollectionmanager.presentation.mapper.toUi
import com.example.mtgcollectionmanager.presentation.model.CardUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CardPrintingsState(
    val isLoading: Boolean = false,
    val printings: List<CardUiModel> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class CardPrintingsViewModel @Inject constructor(
    private val getCardPrintingsUseCase: GetCardPrintingsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(CardPrintingsState())
    val state: StateFlow<CardPrintingsState> = _state.asStateFlow()

    fun loadPrintings(cardName: String) {
        viewModelScope.launch {
            getCardPrintingsUseCase(cardName).collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        _state.update { it.copy(isLoading = resource.isLoading, error = null) }
                    }
                    is Resource.Success -> {
                        resource.data?.let { cards ->
                            _state.update {
                                it.copy(
                                    printings = cards.map { card -> card.toUi() },
                                    isLoading = false,
                                    error = null
                                )
                            }
                        }
                    }
                    is Resource.Error -> {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                error = resource.errorMessage
                            )
                        }
                    }
                }
            }
        }
    }
}
