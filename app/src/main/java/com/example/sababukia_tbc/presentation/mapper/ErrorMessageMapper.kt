package com.example.sababukia_tbc.presentation.mapper

import android.content.Context
import com.example.sababukia_tbc.R
import com.example.sababukia_tbc.domain.common.ErrorType
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ErrorMessageMapper @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun mapToMessage(error: ErrorType): String {
        return when (error) {
            is ErrorType.Network.NoInternet -> context.getString(R.string.error_network_unavailable)
            is ErrorType.Network.Timeout -> context.getString(R.string.error_network_unavailable)
            is ErrorType.Network.Server -> error.message
            is ErrorType.Network.Unknown -> error.message

            is ErrorType.Storage.UploadFailed -> context.getString(R.string.error_upload_failed)
            is ErrorType.Storage.NetworkUnavailable -> context.getString(R.string.error_network_unavailable)
            is ErrorType.Storage.QuotaExceeded -> context.getString(R.string.error_storage_quota_exceeded)
            is ErrorType.Storage.Unknown -> error.message

            is ErrorType.Permission.CameraDenied -> context.getString(R.string.error_camera_permission_denied)
            is ErrorType.Permission.StorageDenied -> context.getString(R.string.error_storage_permission_denied)
            is ErrorType.Permission.PermanentlyDenied -> context.getString(R.string.error_camera_permission_denied)

            is ErrorType.Image.InvalidFormat -> context.getString(R.string.error_invalid_image_format)
            is ErrorType.Image.FileTooLarge -> context.getString(R.string.error_image_too_large, error.maxSizeMb)
            is ErrorType.Image.CompressionFailed -> context.getString(R.string.error_compression_failed)
            is ErrorType.Image.NotSelected -> context.getString(R.string.error_no_image_selected)

            is ErrorType.Generic -> error.message
        }
    }
}
