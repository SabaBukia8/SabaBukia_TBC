package com.example.sababukia_tbc.data.repository

import com.example.sababukia_tbc.data.datasource.local.LocalImageDataSource
import com.example.sababukia_tbc.data.datasource.remote.FirebaseStorageDataSource
import com.example.sababukia_tbc.data.mapper.toDomain
import com.example.sababukia_tbc.domain.common.ErrorType
import com.example.sababukia_tbc.domain.common.Resource
import com.example.sababukia_tbc.domain.model.ImageData
import com.example.sababukia_tbc.domain.model.UploadProgress
import com.example.sababukia_tbc.domain.repository.StorageRepository
import com.google.firebase.storage.StorageException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StorageRepositoryImpl @Inject constructor(
    private val firebaseStorageDataSource: FirebaseStorageDataSource,
    private val localImageDataSource: LocalImageDataSource
) : StorageRepository {

    override fun uploadImage(imageData: ImageData): Flow<Resource<UploadProgress>> = flow {
        emit(Resource.Loading(true))

        val imageBytes = localImageDataSource.readImageBytes(imageData.uri)

        if (imageBytes == null) {
            emit(Resource.Error(ErrorType.Storage.UploadFailed))
            emit(Resource.Loading(false))
            return@flow
        }

        firebaseStorageDataSource.uploadImage(
            fileName = imageData.fileName,
            data = imageBytes,
            mimeType = imageData.mimeType
        ).catch { exception ->
            emit(Resource.Error(mapException(exception)))
            emit(Resource.Loading(false))
        }.collect { result ->
            result.fold(
                onSuccess = { progressDTO ->
                    val progress = progressDTO.toDomain()
                    emit(Resource.Success(progress))
                    if (progress.percentage == 100) {
                        emit(Resource.Loading(false))
                    }
                },
                onFailure = { exception ->
                    emit(Resource.Error(mapException(exception)))
                    emit(Resource.Loading(false))
                }
            )
        }
    }.flowOn(Dispatchers.IO)

    private fun mapException(throwable: Throwable): ErrorType {
        return when (throwable) {
            is StorageException -> {
                when (throwable.errorCode) {
                    StorageException.ERROR_QUOTA_EXCEEDED ->
                        ErrorType.Storage.QuotaExceeded(0L)

                    StorageException.ERROR_NOT_AUTHENTICATED,
                    StorageException.ERROR_NOT_AUTHORIZED ->
                        ErrorType.Storage.UploadFailed

                    StorageException.ERROR_RETRY_LIMIT_EXCEEDED ->
                        ErrorType.Storage.NetworkUnavailable

                    StorageException.ERROR_OBJECT_NOT_FOUND,
                    StorageException.ERROR_BUCKET_NOT_FOUND,
                    StorageException.ERROR_PROJECT_NOT_FOUND ->
                        ErrorType.Storage.ConfigurationError

                    else ->
                        ErrorType.Storage.UploadFailed
                }
            }
            is java.net.UnknownHostException,
            is java.net.SocketTimeoutException ->
                ErrorType.Storage.NetworkUnavailable

            else ->
                ErrorType.Storage.UploadFailed
        }
    }
}
