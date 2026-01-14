package com.example.sababukia_tbc.data.datasource.local

import android.content.ContentResolver
import android.net.Uri
import android.provider.OpenableColumns
import com.example.sababukia_tbc.data.model.dto.ImageDataDTO
import com.example.sababukia_tbc.domain.common.ImageConstants
import java.io.InputStream
import javax.inject.Inject
import javax.inject.Singleton
import androidx.core.net.toUri

@Singleton
class LocalImageDataSource @Inject constructor(
    private val contentResolver: ContentResolver,
    private val uriResolver: UriResolver
) {

    fun getImageMetadata(uriString: String): ImageDataDTO? {
        return try {
            val uri = uriString.toUri()
            val cursor = contentResolver.query(uri, null, null, null, null) ?: return null

            cursor.use {
                if (it.moveToFirst()) {
                    val displayNameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    val sizeIndex = it.getColumnIndex(OpenableColumns.SIZE)

                    val fileName = if (displayNameIndex != -1) {
                        it.getString(displayNameIndex)
                    } else {
                        ImageConstants.generateFileName()
                    }

                    val size = if (sizeIndex != -1) {
                        it.getLong(sizeIndex)
                    } else {
                        0L
                    }

                    val mimeType = contentResolver.getType(uri) ?: ImageConstants.DEFAULT_IMAGE_MIME_TYPE

                    ImageDataDTO(
                        uri = uriString,
                        fileName = fileName,
                        mimeType = mimeType,
                        sizeInBytes = size
                    )
                } else {
                    null
                }
            }
        } catch (e: Exception) {
            null
        }
    }

    // Delegate stream operations to UriResolver
    fun readImageBytes(uriString: String): ByteArray? = uriResolver.readBytes(uriString)

    fun openInputStream(uriString: String): InputStream? = uriResolver.openInputStream(uriString)
}
