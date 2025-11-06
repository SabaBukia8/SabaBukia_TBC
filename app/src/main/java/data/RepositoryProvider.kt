package data

import android.content.Context

object RepositoryProvider {
    @Volatile
    private var cardRepo: CardRepository? = null

    fun cards(context: Context): CardRepository {
        return cardRepo ?: synchronized(this) {
            cardRepo ?: JsonCardRepository(context.applicationContext).also { cardRepo = it }
        }
    }
}
