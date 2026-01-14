package com.example.sababukia_tbc.domain.usecase

import com.example.sababukia_tbc.domain.common.ErrorType
import com.example.sababukia_tbc.domain.common.ImageConstants
import com.example.sababukia_tbc.domain.common.Resource
import com.example.sababukia_tbc.domain.model.ImageData
import javax.inject.Inject

class ValidateImageUseCase @Inject constructor() {

    suspend operator fun invoke(imageData: ImageData): Resource<Unit> {
        val maxSizeBytes = ImageConstants.MAX_IMAGE_SIZE_MB * 1024 * 1024L

        return when {
            !ImageConstants.ALLOWED_IMAGE_FORMATS.contains(imageData.mimeType) -> {
                Resource.Error(ErrorType.Image.InvalidFormat)
            }
            imageData.sizeInBytes > maxSizeBytes -> {
                Resource.Error(ErrorType.Image.FileTooLarge(ImageConstants.MAX_IMAGE_SIZE_MB))
            }
            else -> {
                Resource.Success(Unit)
            }
        }
    }
}
