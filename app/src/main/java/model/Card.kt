package model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class CardType {
    @SerialName("visa") VISA,
    @SerialName("mastercard") MASTERCARD
}

@Serializable
data class Card(
    val id: String,
    val holderName: String,
    val number: String,
    val expiryMonth: Int,
    val expiryYear: Int,
    val cvv: String,
    val type: CardType
)
