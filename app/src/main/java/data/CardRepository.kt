package data

import kotlinx.coroutines.flow.Flow
import model.Card

interface CardRepository {
    val cards: Flow<List<Card>>
    suspend fun refresh()
    suspend fun add(card: Card)
    suspend fun delete(id: String)
}
