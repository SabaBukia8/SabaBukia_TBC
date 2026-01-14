package com.example.sababukia_tbc.presentation.screen.imageupload

sealed class ImageUploadEvent {
    data object AddImageClicked : ImageUploadEvent()
    data object CameraSelected : ImageUploadEvent()
    data object GallerySelected : ImageUploadEvent()
    data object UploadClicked : ImageUploadEvent()
}
