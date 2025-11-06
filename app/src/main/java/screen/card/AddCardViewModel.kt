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

class AddCardViewModel(private val repository: CardRepository) : ViewModel() {

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

    private val _state = MutableStateFlow(FormState())
    val state: StateFlow<FormState> = _state.asStateFlow()

    fun updateName(value: String) = update { copy(holderName = value).validate() }
    fun updateNumber(value: String) = update { copy(number = value.filter { it.isDigit() }.take(CardConstants.CARD_NUMBER_LENGTH)).validate() }
    fun updateMonth(value: String) = update { copy(expiryMonth = value.filter { it.isDigit() }.take(2)).validate() }
    fun updateYear(value: String) = update { copy(expiryYear = value.filter { it.isDigit() }.take(4)).validate() }
    fun updateCvv(value: String) = update { copy(cvv = value.filter { it.isDigit() }.take(CardConstants.CVV_LENGTH)).validate() }
    fun updateType(value: CardType) = update { copy(type = value).validate() }

    private inline fun update(block: FormState.() -> FormState) { _state.value = _state.value.block() }

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

    fun submit() {
        val s = _state.value.validate()
        if (!s.isValid) {
            _state.value = s.copy(error = "Form is not valid")
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
            _state.value = s.copy(submitted = true)
        }
    }
}
