package com.example.sababukia_tbc.domain.common

object ImageConstants {
    const val MAX_IMAGE_SIZE_MB = 10
    val ALLOWED_IMAGE_FORMATS = listOf(
        "image/jpeg",
        "image/jpg",
        "image/png",
        "image/webp"
    )

    const val DEFAULT_COMPRESSION_QUALITY = 80
    const val MAX_IMAGE_WIDTH = 2048
    const val MAX_IMAGE_HEIGHT = 2048

    const val DEFAULT_IMAGE_MIME_TYPE = "image/jpeg"
    const val COMPRESSED_IMAGE_PREFIX = "compressed_"
    const val CAMERA_IMAGE_PREFIX = "camera_"
    const val DEFAULT_IMAGE_PREFIX = "image_"
    const val DEFAULT_IMAGE_EXTENSION = ".jpg"

    const val FIREBASE_IMAGES_PATH = "images"

    fun generateFileName(prefix: String = DEFAULT_IMAGE_PREFIX): String {
        return "${prefix}${System.currentTimeMillis()}${DEFAULT_IMAGE_EXTENSION}"
    }
}
