package com.example.sababukia_tbc.domain.usecase

import com.example.sababukia_tbc.domain.common.Resource
import com.example.sababukia_tbc.domain.model.ImageData
import com.example.sababukia_tbc.domain.repository.ImageRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ExtractImageMetadataUseCase @Inject constructor(
    private val imageRepository: ImageRepository
) {
    operator fun invoke(uriString: String): Flow<Resource<ImageData>> {
        return imageRepository.extractImageMetadata(uriString)
    }
}
