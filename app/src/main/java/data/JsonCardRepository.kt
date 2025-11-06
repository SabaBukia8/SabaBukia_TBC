package data

import android.content.Context
import androidx.annotation.VisibleForTesting
import data.CardConstants.ASSET_CARDS_PATH
import data.CardConstants.CARDS_FILE_NAME
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import model.Card
import java.io.File

class JsonCardRepository(
    private val appContext: Context,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val json: Json = defaultJson
) : CardRepository {

    private val _cards = MutableStateFlow<List<Card>>(emptyList())
    override val cards: Flow<List<Card>> = _cards.asStateFlow()

    private val file: File by lazy { File(appContext.filesDir, CARDS_FILE_NAME) }

    override suspend fun refresh() = withContext(ioDispatcher) {
        ensureSeed()
        _cards.value = readCards()
    }

    override suspend fun add(card: Card) = withContext(ioDispatcher) {
        val list = readCards().toMutableList().apply { add(card) }
        writeCards(list)
        _cards.value = list
    }

    override suspend fun delete(id: String) = withContext(ioDispatcher) {
        val list = readCards().filterNot { it.id == id }
        writeCards(list)
        _cards.value = list
    }

    private fun readCards(): List<Card> {
        if (!file.exists()) return emptyList()
        val text = file.readText()
        if (text.isBlank()) return emptyList()
        return json.decodeFromString(text)
    }

    private fun writeCards(list: List<Card>) {
        val tmp = File(file.parentFile, "${file.name}.tmp")
        tmp.writeText(json.encodeToString(list))
        if (file.exists()) file.delete()
        tmp.renameTo(file)
    }

    private fun ensureSeed() {
        if (file.exists()) return
        file.parentFile?.mkdirs()
        appContext.assets.open(ASSET_CARDS_PATH).use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }
    }

    companion object {
        @VisibleForTesting
        val defaultJson = Json { prettyPrint = true; ignoreUnknownKeys = true }
    }
}
