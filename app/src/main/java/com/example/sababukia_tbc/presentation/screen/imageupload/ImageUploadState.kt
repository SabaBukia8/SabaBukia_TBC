package com.example.sababukia_tbc.presentation.screen.imageupload

import com.example.sababukia_tbc.domain.model.ImageData

data class ImageUploadState(
    val selectedImage: ImageData? = null,
    val isLoading: Boolean = false,
    val uploadProgress: Int = 0,
    val isUploading: Boolean = false
)
