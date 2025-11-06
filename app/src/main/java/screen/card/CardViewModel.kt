package screen.card

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import data.CardRepository
import data.CardConstants
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import model.Card
import model.CardType
import java.util.UUID

class CardViewModel(private val repository: CardRepository) : ViewModel() {

    data class FormState(
        val holderName: String = "",
        val number: String = "",
        val expiryMonth: String = "",
        val expiryYear: String = "",
        val cvv: String = "",
        val type: CardType = CardType.MASTERCARD,
        val isValid: Boolean = false,
        val error: String? = null,
        val submitted: Boolean = false
    )

    private val _cards = MutableStateFlow<List<Card>>(emptyList())
    val cards: StateFlow<List<Card>> = _cards.asStateFlow()

    private val _formState = MutableStateFlow(FormState())
    val formState: StateFlow<FormState> = _formState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.refresh()
        }
        viewModelScope.launch {
            repository.cards.collect { _cards.value = it }
        }
    }

    fun updateName(value: String) = updateForm { copy(holderName = value).validate() }

    fun updateNumber(value: String) = updateForm {
        copy(number = value.filter { it.isDigit() }.take(CardConstants.CARD_NUMBER_LENGTH)).validate()
    }

    fun updateMonth(value: String) = updateForm {
        copy(expiryMonth = value.filter { it.isDigit() }.take(2)).validate()
    }

    fun updateYear(value: String) = updateForm {
        copy(expiryYear = value.filter { it.isDigit() }.take(4)).validate()
    }

    fun updateCvv(value: String) = updateForm {
        copy(cvv = value.filter { it.isDigit() }.take(CardConstants.CVV_LENGTH)).validate()
    }

    fun updateType(value: CardType) = updateForm { copy(type = value).validate() }

    private inline fun updateForm(block: FormState.() -> FormState) {
        _formState.value = _formState.value.block()
    }

    private fun FormState.validate(): FormState {
        val nameOk = holderName.trim().length >= CardConstants.MIN_NAME_LENGTH
        val numberOk = number.length == CardConstants.CARD_NUMBER_LENGTH
        val month = expiryMonth.toIntOrNull() ?: 0
        val year = expiryYear.toIntOrNull() ?: 0
        val monthOk = month in 1..12
        val yearOk = year in 2024..2100
        val cvvOk = cvv.length == CardConstants.CVV_LENGTH
        val ok = nameOk && numberOk && monthOk && yearOk && cvvOk
        return copy(isValid = ok, error = null)
    }

    fun submitCard() {
        val s = _formState.value.validate()
        if (!s.isValid) {
            _formState.value = s.copy(error = "Form is not valid")
            return
        }
        viewModelScope.launch {
            val card = Card(
                id = UUID.randomUUID().toString(),
                holderName = s.holderName.trim(),
                number = s.number,
                expiryMonth = s.expiryMonth.toInt(),
                expiryYear = s.expiryYear.toInt(),
                cvv = s.cvv,
                type = s.type
            )
            repository.add(card)
            _formState.value = s.copy(submitted = true)
        }
    }

    fun resetForm() {
        _formState.value = FormState()
    }

    fun deleteCard(id: String) {
        viewModelScope.launch {
            repository.delete(id)
        }
    }
}