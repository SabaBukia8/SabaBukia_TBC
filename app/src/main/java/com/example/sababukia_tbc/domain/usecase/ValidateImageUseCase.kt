package com.example.sababukia_tbc.domain.usecase

import com.example.sababukia_tbc.domain.common.ErrorType
import com.example.sababukia_tbc.domain.common.ImageConstants
import com.example.sababukia_tbc.domain.common.Resource
import com.example.sababukia_tbc.domain.model.ImageData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ValidateImageUseCase @Inject constructor() {

    operator fun invoke(imageData: ImageData): Flow<Resource<Unit>> = flow {
        val maxSizeBytes = ImageConstants.MAX_IMAGE_SIZE_MB * 1024 * 1024L

        when {
            !ImageConstants.ALLOWED_IMAGE_FORMATS.contains(imageData.mimeType) -> {
                emit(Resource.Error(ErrorType.Image.InvalidFormat))
            }
            imageData.sizeInBytes > maxSizeBytes -> {
                emit(Resource.Error(ErrorType.Image.FileTooLarge(ImageConstants.MAX_IMAGE_SIZE_MB)))
            }
            else -> {
                emit(Resource.Success(Unit))
            }
        }
    }
}
