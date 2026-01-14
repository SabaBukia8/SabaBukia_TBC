package com.example.sababukia_tbc.domain.usecase

import com.example.sababukia_tbc.domain.common.Resource
import com.example.sababukia_tbc.domain.model.ImageData
import com.example.sababukia_tbc.domain.model.UploadProgress
import com.example.sababukia_tbc.domain.repository.StorageRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UploadImageToStorageUseCase @Inject constructor(
    private val storageRepository: StorageRepository
) {
    operator fun invoke(imageData: ImageData): Flow<Resource<UploadProgress>> {
        return storageRepository.uploadImage(imageData)
    }
}
