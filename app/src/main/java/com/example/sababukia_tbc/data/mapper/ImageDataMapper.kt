package com.example.sababukia_tbc.data.mapper

import com.example.sababukia_tbc.data.model.dto.ImageDataDTO
import com.example.sababukia_tbc.domain.model.ImageData

fun ImageDataDTO.toDomain(): ImageData {
    return ImageData(
        uri = uri,
        fileName = fileName,
        mimeType = mimeType,
        sizeInBytes = sizeInBytes
    )
}

fun ImageData.toDTO(): ImageDataDTO {
    return ImageDataDTO(
        uri = uri,
        fileName = fileName,
        mimeType = mimeType,
        sizeInBytes = sizeInBytes
    )
}
