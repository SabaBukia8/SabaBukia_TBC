package com.example.sababukia_tbc.domain.model

data class UploadProgress(
    val bytesTransferred: Long,
    val totalBytes: Long
) {
    val percentage: Int
        get() = if (totalBytes > 0) {
            ((bytesTransferred * 100) / totalBytes).toInt()
        } else {
            0
        }
}
