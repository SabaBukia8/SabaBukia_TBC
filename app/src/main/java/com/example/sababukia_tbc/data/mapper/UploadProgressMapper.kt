package com.example.sababukia_tbc.data.mapper

import com.example.sababukia_tbc.data.model.dto.UploadProgressDTO
import com.example.sababukia_tbc.domain.model.UploadProgress

fun UploadProgressDTO.toDomain(): UploadProgress {
    return UploadProgress(
        bytesTransferred = bytesTransferred,
        totalBytes = totalBytes
    )
}
