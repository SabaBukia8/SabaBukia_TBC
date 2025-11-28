package com.example.sababukia_tbc.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.Serializer
import androidx.datastore.dataStoreFile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

class ProtoDataStoreManager<T>(
    private val dataStore: DataStore<T>
) {
    val data: Flow<T> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(dataStore.data.first())
            } else {
                throw exception
            }
        }

    fun <R> read(transform: (T) -> R): Flow<R> = data.map(transform)

    suspend fun write(transform: suspend (T) -> T) {
        dataStore.updateData(transform)
    }

    suspend fun clear(defaultValue: T) {
        dataStore.updateData { defaultValue }
    }

    companion object {
        fun <T> create(
            context: Context,
            fileName: String,
            serializer: Serializer<T>
        ): ProtoDataStoreManager<T> {
            val dataStore = DataStoreFactory.create(
                serializer = serializer,
                produceFile = { context.dataStoreFile(fileName) }
            )
            return ProtoDataStoreManager(dataStore)
        }
    }
}
