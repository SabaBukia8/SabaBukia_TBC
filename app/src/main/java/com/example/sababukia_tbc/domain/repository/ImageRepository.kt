package com.example.sababukia_tbc.domain.repository

import com.example.sababukia_tbc.domain.common.Resource
import com.example.sababukia_tbc.domain.model.ImageData
import kotlinx.coroutines.flow.Flow

interface ImageRepository {
    fun getImageFromCamera(outputUriString: String): Flow<Resource<ImageData>>
    fun getImageFromGallery(selectedUriString: String): Flow<Resource<ImageData>>
    fun compressImage(imageData: ImageData, quality: Int): Flow<Resource<ImageData>>
    fun validateImage(imageData: ImageData): Flow<Resource<Unit>>
}
