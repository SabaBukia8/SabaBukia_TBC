package com.example.sababukia_tbc.data.datasource.local

import android.content.ContentResolver
import android.net.Uri
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import javax.inject.Inject
import javax.inject.Singleton
import androidx.core.net.toUri

@Singleton
class UriResolver @Inject constructor(
    private val contentResolver: ContentResolver
) {

    fun openInputStream(uriString: String): InputStream? {
        return try {
            when {
                !uriString.contains("://") -> {
                    val file = File(uriString)
                    if (file.exists()) FileInputStream(file) else null
                }
                uriString.startsWith("file://") -> {
                    val filePath = uriString.removePrefix("file://")
                    val file = File(filePath)
                    if (file.exists()) FileInputStream(file) else null
                }
                else -> {
                    val uri = uriString.toUri()
                    contentResolver.openInputStream(uri)
                }
            }
        } catch (e: Exception) {
            null
        }
    }

    fun readBytes(uriString: String): ByteArray? {
        return openInputStream(uriString)?.use { it.readBytes() }
    }
}
