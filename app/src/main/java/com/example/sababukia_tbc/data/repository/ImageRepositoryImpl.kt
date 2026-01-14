package com.example.sababukia_tbc.data.repository

import com.example.sababukia_tbc.data.common.ImageCompressor
import com.example.sababukia_tbc.data.datasource.local.LocalImageDataSource
import com.example.sababukia_tbc.data.mapper.toDomain
import com.example.sababukia_tbc.domain.common.ErrorType
import com.example.sababukia_tbc.domain.common.ImageConstants
import com.example.sababukia_tbc.domain.common.Resource
import com.example.sababukia_tbc.domain.model.ImageData
import com.example.sababukia_tbc.domain.repository.ImageRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImageRepositoryImpl @Inject constructor(
    private val localImageDataSource: LocalImageDataSource,
    private val imageCompressor: ImageCompressor,
    private val cacheDir: File
) : ImageRepository {

    override fun getImageFromCamera(outputUriString: String): Flow<Resource<ImageData>> = flow {
        emit(Resource.Loading(true))
        try {
            val metadata = localImageDataSource.getImageMetadata(outputUriString)
            if (metadata != null) {
                emit(Resource.Success(metadata.toDomain()))
            } else {
                emit(Resource.Error(ErrorType.Image.NotSelected))
            }
        } catch (e: Exception) {
            emit(Resource.Error(ErrorType.Generic(e.message ?: "Camera error")))
        }
        emit(Resource.Loading(false))
    }.flowOn(Dispatchers.IO)

    override fun getImageFromGallery(selectedUriString: String): Flow<Resource<ImageData>> = flow {
        emit(Resource.Loading(true))
        try {
            val metadata = localImageDataSource.getImageMetadata(selectedUriString)
            if (metadata != null) {
                emit(Resource.Success(metadata.toDomain()))
            } else {
                emit(Resource.Error(ErrorType.Image.NotSelected))
            }
        } catch (e: Exception) {
            emit(Resource.Error(ErrorType.Generic(e.message ?: "Gallery error")))
        }
        emit(Resource.Loading(false))
    }.flowOn(Dispatchers.IO)

    override fun compressImage(imageData: ImageData, quality: Int): Flow<Resource<ImageData>> = flow {
        emit(Resource.Loading(true))

        val compressedFile = File(
            cacheDir,
            ImageConstants.generateFileName(ImageConstants.COMPRESSED_IMAGE_PREFIX)
        )
        val inputStream = localImageDataSource.openInputStream(imageData.uri)

        if (inputStream == null) {
            emit(Resource.Error(ErrorType.Image.CompressionFailed))
            emit(Resource.Loading(false))
            return@flow
        }

        imageCompressor.compress(inputStream, quality, compressedFile)
            .onSuccess {
                if (compressedFile.exists()) {
                    val compressedImageData = ImageData(
                        uri = compressedFile.absolutePath,
                        fileName = compressedFile.name,
                        mimeType = ImageConstants.DEFAULT_IMAGE_MIME_TYPE,
                        sizeInBytes = compressedFile.length()
                    )
                    emit(Resource.Success(compressedImageData))
                } else {
                    emit(Resource.Error(ErrorType.Image.CompressionFailed))
                }
            }
            .onFailure { exception ->
                emit(Resource.Error(
                    when (exception) {
                        is OutOfMemoryError ->
                            ErrorType.Image.CompressionFailed
                        is IllegalStateException ->
                            ErrorType.Image.CompressionFailed
                        else ->
                            ErrorType.Generic(exception.message ?: "Compression failed")
                    }
                ))
            }

        emit(Resource.Loading(false))
    }.flowOn(Dispatchers.IO)

    override fun validateImage(imageData: ImageData): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading(true))
        emit(Resource.Success(Unit))
        emit(Resource.Loading(false))
    }
}
