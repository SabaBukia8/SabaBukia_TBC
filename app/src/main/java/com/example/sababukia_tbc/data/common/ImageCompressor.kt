package com.example.sababukia_tbc.data.common

import java.io.File
import java.io.InputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImageCompressor @Inject constructor(
    private val bitmapDecoder: BitmapDecoder,
    private val bitmapEncoder: BitmapEncoder
) {

    fun compress(
        inputStream: InputStream,
        quality: Int,
        outputFile: File
    ): Result<Unit> {
        return runCatching {
            val imageBytes = inputStream.use { it.readBytes() }
            val decodedBitmap = bitmapDecoder.decode(imageBytes)
                ?: return Result.failure(IllegalStateException("Failed to decode bitmap"))

            try {
                bitmapEncoder.encodeToFile(decodedBitmap.bitmap, outputFile, quality)
                    .getOrThrow()
            } finally {
                decodedBitmap.bitmap.recycle()
            }
        }
    }

    fun compressToBytes(
        inputStream: InputStream,
        quality: Int
    ): Result<ByteArray> {
        return runCatching {
            val imageBytes = inputStream.use { it.readBytes() }
            val decodedBitmap = bitmapDecoder.decode(imageBytes)
                ?: return Result.failure(IllegalStateException("Failed to decode bitmap"))

            try {
                bitmapEncoder.encodeToBytes(decodedBitmap.bitmap, quality)
                    .getOrThrow()
            } finally {
                decodedBitmap.bitmap.recycle()
            }
        }
    }
}
