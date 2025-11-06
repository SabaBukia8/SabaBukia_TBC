package screen.card

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import data.CardRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import model.Card

class CardListViewModel(private val repository: CardRepository) : ViewModel() {

    private val _cards = MutableStateFlow<List<Card>>(emptyList())
    val cards: StateFlow<List<Card>> = _cards.asStateFlow()

    init {
        viewModelScope.launch { repository.refresh() }
        viewModelScope.launch { repository.cards.collect { _cards.value = it } }
    }

    fun deleteCard(id: String) {
        viewModelScope.launch { repository.delete(id) }
    }
}
