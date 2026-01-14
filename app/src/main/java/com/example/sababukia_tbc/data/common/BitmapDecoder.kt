package com.example.sababukia_tbc.data.common

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.example.sababukia_tbc.domain.common.ImageConstants
import javax.inject.Inject
import javax.inject.Singleton
@Singleton
class BitmapDecoder @Inject constructor() {

    data class DecodedBitmap(
        val bitmap: Bitmap,
        val originalWidth: Int,
        val originalHeight: Int
    )

    fun decode(
        imageBytes: ByteArray,
        maxWidth: Int = ImageConstants.MAX_IMAGE_WIDTH,
        maxHeight: Int = ImageConstants.MAX_IMAGE_HEIGHT
    ): DecodedBitmap? {
        val options = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }
        BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size, options)

        val originalWidth = options.outWidth
        val originalHeight = options.outHeight

        options.inSampleSize = calculateInSampleSize(options, maxWidth, maxHeight)
        options.inJustDecodeBounds = false

        val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size, options)

        return bitmap?.let {
            DecodedBitmap(it, originalWidth, originalHeight)
        }
    }

    private fun calculateInSampleSize(
        options: BitmapFactory.Options,
        reqWidth: Int,
        reqHeight: Int
    ): Int {
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            val halfHeight = height / 2
            val halfWidth = width / 2

            while (halfHeight / inSampleSize >= reqHeight &&
                halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }

        return inSampleSize
    }
}
