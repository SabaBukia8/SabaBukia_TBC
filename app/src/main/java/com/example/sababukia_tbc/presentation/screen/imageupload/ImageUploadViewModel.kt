package com.example.sababukia_tbc.presentation.screen.imageupload

import androidx.lifecycle.viewModelScope
import com.example.sababukia_tbc.domain.common.ErrorType
import com.example.sababukia_tbc.domain.common.Resource
import com.example.sababukia_tbc.domain.model.ImageData
import com.example.sababukia_tbc.domain.usecase.CompressImageUseCase
import com.example.sababukia_tbc.domain.usecase.UploadImageToStorageUseCase
import com.example.sababukia_tbc.domain.usecase.ValidateImageUseCase
import com.example.sababukia_tbc.presentation.common.BaseViewModel
import com.example.sababukia_tbc.presentation.mapper.ErrorMessageMapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ImageUploadViewModel @Inject constructor(
    private val validateImageUseCase: ValidateImageUseCase,
    private val compressImageUseCase: CompressImageUseCase,
    private val uploadImageToStorageUseCase: UploadImageToStorageUseCase,
    private val errorMessageMapper: ErrorMessageMapper
) : BaseViewModel<ImageUploadState, ImageUploadEvent, ImageUploadSideEffect>(
    initialState = ImageUploadState()
) {

    override fun onEvent(event: ImageUploadEvent) {
        when (event) {
            is ImageUploadEvent.AddImageClicked -> handleAddImageClicked()
            is ImageUploadEvent.CameraSelected -> handleCameraSelected()
            is ImageUploadEvent.GallerySelected -> handleGallerySelected()
            is ImageUploadEvent.UploadClicked -> handleUploadClicked()
        }
    }

    private fun handleAddImageClicked() {
        sendSideEffect(ImageUploadSideEffect.ShowImageSelectionBottomSheet)
    }

    private fun handleCameraSelected() {
        sendSideEffect(ImageUploadSideEffect.RequestCameraPermission)
    }

    private fun handleGallerySelected() {
        sendSideEffect(ImageUploadSideEffect.RequestStoragePermission)
    }

    fun onCameraPermissionGranted(outputUriString: String) {
        sendSideEffect(ImageUploadSideEffect.DismissImageSelectionBottomSheet)
        sendSideEffect(ImageUploadSideEffect.OpenCamera(outputUriString))
    }

    fun onStoragePermissionGranted() {
        sendSideEffect(ImageUploadSideEffect.DismissImageSelectionBottomSheet)
        sendSideEffect(ImageUploadSideEffect.OpenGallery)
    }

    fun onImageSelected(imageData: ImageData) {
        viewModelScope.launch {
            validateImageUseCase(imageData)
                .flatMapLatest { result ->
                    when (result) {
                        is Resource.Loading -> flow { emit(result) }
                        is Resource.Error -> flow { emit(result) }
                        is Resource.Success -> compressImageUseCase(imageData)
                    }
                }
                .catch { exception ->
                    updateState { copy(isLoading = false) }
                    sendSideEffect(ImageUploadSideEffect.ShowError(
                        errorMessageMapper.mapToMessage(
                            ErrorType.Generic(exception.message ?: "Unknown error")
                        )
                    ))
                }
                .collect { resource ->
                    when (resource) {
                        is Resource.Loading ->
                            updateState { copy(isLoading = resource.isLoading) }

                        is Resource.Error -> {
                            updateState { copy(isLoading = false) }
                            sendSideEffect(ImageUploadSideEffect.ShowError(
                                errorMessageMapper.mapToMessage(resource.error)
                            ))
                        }

                        is Resource.Success ->
                            updateState {
                                copy(
                                    isLoading = false,
                                    selectedImage = resource.data
                                )
                            }
                    }
                }
        }
    }

    private fun handleUploadClicked() {
        val image = currentState.selectedImage ?: return

        viewModelScope.launch {
            collectResource(
                flow = uploadImageToStorageUseCase(image),
                onLoading = { isLoading ->
                    updateState { copy(isUploading = isLoading) }
                },
                onError = { error ->
                    updateState { copy(isUploading = false, uploadProgress = 0) }
                    sendSideEffect(ImageUploadSideEffect.ShowError(
                        errorMessageMapper.mapToMessage(error)
                    ))
                },
                onSuccess = { progress ->
                    updateState { copy(uploadProgress = progress.percentage) }
                    if (progress.percentage == 100) {
                        updateState {
                            copy(
                                isUploading = false,
                                uploadProgress = 0,
                                selectedImage = null
                            )
                        }
                        sendSideEffect(ImageUploadSideEffect.UploadSuccess)
                    }
                }
            )
        }
    }
}
