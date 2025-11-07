// repository/FormRepository.kt
package repository

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import model.FormField
import model.FormSection

class FormRepository(private val context: Context) {

    private val json = Json { ignoreUnknownKeys = true }

    suspend fun loadFormConfiguration(): List<FormSection> = withContext(Dispatchers.IO) {
        try {
            val jsonString = context.assets.open("form_config.json")
                .bufferedReader()
                .use { it.readText() }

            val fieldGroups: List<List<FormField>> = json.decodeFromString(jsonString)

            fieldGroups.map { FormSection(it.filter { field -> field.isActive }) }
        } catch (e: Exception) {
            emptyList()
        }
    }
}