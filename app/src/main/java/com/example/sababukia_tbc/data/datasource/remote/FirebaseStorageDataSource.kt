package com.example.sababukia_tbc.data.datasource.remote

import com.example.sababukia_tbc.data.model.dto.UploadProgressDTO
import com.example.sababukia_tbc.domain.common.ImageConstants
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageException
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseStorageDataSource @Inject constructor(
    private val firebaseStorage: FirebaseStorage
) {

    fun uploadImage(
        fileName: String,
        data: ByteArray,
        mimeType: String
    ): Flow<Result<UploadProgressDTO>> = callbackFlow {
        val timestamp = System.currentTimeMillis()
        val storageRef = firebaseStorage.reference
            .child(ImageConstants.FIREBASE_IMAGES_PATH)
            .child("${timestamp}_${fileName}")

        val metadata = com.google.firebase.storage.StorageMetadata.Builder()
            .setContentType(mimeType)
            .build()

        val uploadTask = storageRef.putBytes(data, metadata)

        uploadTask.addOnProgressListener { taskSnapshot ->
            val progress = UploadProgressDTO(
                bytesTransferred = taskSnapshot.bytesTransferred,
                totalBytes = taskSnapshot.totalByteCount
            )
            trySend(Result.success(progress))
        }.addOnSuccessListener {
            val finalProgress = UploadProgressDTO(
                bytesTransferred = it.totalByteCount,
                totalBytes = it.totalByteCount
            )
            trySend(Result.success(finalProgress))
            close()
        }.addOnFailureListener { exception ->
            trySend(Result.failure(exception))
            close(exception)
        }

        awaitClose {
            uploadTask.cancel()
        }
    }
}
