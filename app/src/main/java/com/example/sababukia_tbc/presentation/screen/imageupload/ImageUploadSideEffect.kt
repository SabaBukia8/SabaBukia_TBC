package com.example.sababukia_tbc.presentation.screen.imageupload

sealed class ImageUploadSideEffect {
    data class OpenCamera(val outputUriString: String) : ImageUploadSideEffect()
    data object OpenGallery : ImageUploadSideEffect()
    data class ShowError(val message: String) : ImageUploadSideEffect()
    data object ShowImageSelectionBottomSheet : ImageUploadSideEffect()
    data object DismissImageSelectionBottomSheet : ImageUploadSideEffect()
    data object UploadSuccess : ImageUploadSideEffect()
    data object RequestCameraPermission : ImageUploadSideEffect()
    data object RequestStoragePermission : ImageUploadSideEffect()
}
