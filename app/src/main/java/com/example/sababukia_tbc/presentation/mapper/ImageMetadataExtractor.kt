package com.example.sababukia_tbc.presentation.mapper

import android.content.ContentResolver
import android.net.Uri
import android.provider.OpenableColumns
import com.example.sababukia_tbc.domain.common.ImageConstants
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImageMetadataExtractor @Inject constructor() {

    data class ImageMetadata(
        val uri: String,
        val fileName: String,
        val mimeType: String,
        val sizeInBytes: Long
    )

    fun extract(uri: Uri, contentResolver: ContentResolver): ImageMetadata {
        val cursor = contentResolver.query(uri, null, null, null, null)

        return cursor?.use {
            if (it.moveToFirst()) {
                extractFromCursor(it, uri, contentResolver)
            } else {
                createFallback(uri, contentResolver)
            }
        } ?: createFallback(uri, contentResolver)
    }

    private fun extractFromCursor(
        cursor: android.database.Cursor,
        uri: Uri,
        contentResolver: ContentResolver
    ): ImageMetadata {
        val displayNameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)

        val fileName = if (displayNameIndex != -1) {
            cursor.getString(displayNameIndex)
        } else {
            ImageConstants.generateFileName()
        }

        val size = if (sizeIndex != -1) {
            cursor.getLong(sizeIndex)
        } else {
            0L
        }

        val mimeType = contentResolver.getType(uri) ?: ImageConstants.DEFAULT_IMAGE_MIME_TYPE

        return ImageMetadata(
            uri = uri.toString(),
            fileName = fileName,
            mimeType = mimeType,
            sizeInBytes = size
        )
    }

    private fun createFallback(uri: Uri, contentResolver: ContentResolver): ImageMetadata {
        return ImageMetadata(
            uri = uri.toString(),
            fileName = ImageConstants.generateFileName(),
            mimeType = contentResolver.getType(uri) ?: ImageConstants.DEFAULT_IMAGE_MIME_TYPE,
            sizeInBytes = 0L
        )
    }
}
