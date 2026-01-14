package com.example.sababukia_tbc.domain.repository

import com.example.sababukia_tbc.domain.common.Resource
import com.example.sababukia_tbc.domain.model.ImageData
import com.example.sababukia_tbc.domain.model.UploadProgress
import kotlinx.coroutines.flow.Flow

interface StorageRepository {
    fun uploadImage(imageData: ImageData): Flow<Resource<UploadProgress>>
}
