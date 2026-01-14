package com.example.sababukia_tbc.data.common

import android.graphics.Bitmap
import com.example.sababukia_tbc.domain.common.ImageConstants
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BitmapEncoder @Inject constructor() {

    fun encodeToFile(
        bitmap: Bitmap,
        outputFile: File,
        quality: Int = ImageConstants.DEFAULT_COMPRESSION_QUALITY,
        format: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG
    ): Result<Unit> {
        return runCatching {
            FileOutputStream(outputFile).use { outputStream ->
                bitmap.compress(format, quality, outputStream)
                outputStream.flush()
            }
        }
    }

    fun encodeToBytes(
        bitmap: Bitmap,
        quality: Int = ImageConstants.DEFAULT_COMPRESSION_QUALITY,
        format: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG
    ): Result<ByteArray> {
        return runCatching {
            ByteArrayOutputStream().use { outputStream ->
                bitmap.compress(format, quality, outputStream)
                outputStream.toByteArray()
            }
        }
    }
}
