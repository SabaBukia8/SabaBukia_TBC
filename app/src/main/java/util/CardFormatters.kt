package util

import model.Card

object CardFormatters {
    fun maskNumber(number: String): String {
        val clean = number.filter { it.isDigit() }
        val masked = clean.mapIndexed { index, c -> if (index < clean.length - 4) '*' else c }
        return masked.chunked(4).joinToString(" ") { it.joinToString("") }
    }

    fun displayExpiry(month: Int, year: Int): String {
        val m = month.coerceIn(1, 12).toString().padStart(2, '0')
        val y = (year % 100).toString().padStart(2, '0')
        return "$m/$y"
    }

    fun last4(card: Card): String = card.number.takeLast(4)
}
