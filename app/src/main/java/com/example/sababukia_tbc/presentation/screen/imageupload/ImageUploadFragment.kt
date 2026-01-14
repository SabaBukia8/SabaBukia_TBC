package com.example.sababukia_tbc.presentation.screen.imageupload

import android.Manifest
import android.net.Uri
import android.os.Build
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.viewModels
import coil.load
import com.example.sababukia_tbc.R
import com.example.sababukia_tbc.databinding.FragmentImageUploadBinding
import com.example.sababukia_tbc.presentation.common.BaseFragment
import com.example.sababukia_tbc.presentation.extension.disable
import com.example.sababukia_tbc.presentation.extension.enable
import com.example.sababukia_tbc.presentation.extension.hide
import com.example.sababukia_tbc.presentation.extension.onClick
import com.example.sababukia_tbc.domain.common.ImageConstants
import com.example.sababukia_tbc.presentation.extension.show
import com.example.sababukia_tbc.presentation.extension.showSnackbar
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import androidx.core.net.toUri

@AndroidEntryPoint
class ImageUploadFragment : BaseFragment<FragmentImageUploadBinding>(
    FragmentImageUploadBinding::inflate
) {

    private val viewModel: ImageUploadViewModel by viewModels()

    private var currentPhotoUri: Uri? = null

    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            currentPhotoUri?.let { uri ->
                handleImageSelected(uri)
            }
        }
    }

    private val galleryLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { handleImageSelected(it) }
    }

    private val photoPickerLauncher = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri?.let { handleImageSelected(it) }
    }

    private val cameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            currentPhotoUri = createImageUri()
            currentPhotoUri?.let {
                viewModel.onCameraPermissionGranted(it.toString())
            }
        } else {
            showPermissionDeniedMessage(R.string.error_camera_permission_denied)
        }
    }

    private val storagePermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            viewModel.onStoragePermissionGranted()
        } else {
            showPermissionDeniedMessage(R.string.error_storage_permission_denied)
        }
    }

    override fun setupViews() {
        binding.btnUpload.disable()
    }

    override fun setupListeners() {
        binding.apply {
            btnAddImage.onClick {
                viewModel.onEvent(ImageUploadEvent.AddImageClicked)
            }

            btnUpload.onClick {
                viewModel.onEvent(ImageUploadEvent.UploadClicked)
            }
        }
    }

    override fun observeState() {
        collectStateFlow(viewModel.state) { state ->
            handleState(state)
        }

        collectFlow(viewModel.sideEffect) { effect ->
            handleSideEffect(effect)
        }
    }

    private fun handleState(state: ImageUploadState) {
        binding.apply {
            if (state.isLoading) {
                progressBar.show()
            } else {
                progressBar.hide()
            }

            state.selectedImage?.let { image ->
                ivSelectedImage.show()
                ivSelectedImage.load(image.uri.toUri()) {
                    crossfade(true)
                }
                btnUpload.enable()
            } ?: run {
                ivSelectedImage.hide()
                btnUpload.disable()
            }

            if (state.isUploading) {
                uploadProgressBar.show()
                uploadProgressBar.progress = state.uploadProgress
                tvUploadProgress.show()
                tvUploadProgress.text = getString(R.string.upload_progress, state.uploadProgress)
                btnUpload.disable()
                btnAddImage.disable()
            } else {
                uploadProgressBar.hide()
                tvUploadProgress.hide()
                btnAddImage.enable()
                state.selectedImage?.let { btnUpload.enable() }
            }
        }
    }

    private fun handleSideEffect(effect: ImageUploadSideEffect) {
        when (effect) {
            is ImageUploadSideEffect.ShowImageSelectionBottomSheet -> {
                ImageSelectionBottomSheet().show(childFragmentManager, "image_selection")
            }
            is ImageUploadSideEffect.DismissImageSelectionBottomSheet -> {
                (childFragmentManager.findFragmentByTag("image_selection") as? ImageSelectionBottomSheet)
                    ?.dismiss()
            }
            is ImageUploadSideEffect.OpenCamera -> {
                cameraLauncher.launch(effect.outputUriString.toUri())
            }
            is ImageUploadSideEffect.OpenGallery -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    photoPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                } else {
                    galleryLauncher.launch("image/*")
                }
            }
            is ImageUploadSideEffect.RequestCameraPermission -> {
                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
            is ImageUploadSideEffect.RequestStoragePermission -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    storagePermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                } else {
                    storagePermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                }
            }
            is ImageUploadSideEffect.ShowError -> {
                binding.root.showSnackbar(effect.message, Snackbar.LENGTH_LONG)
            }
            is ImageUploadSideEffect.UploadSuccess -> with(binding) {
                root.showSnackbar(getString(R.string.upload_success), Snackbar.LENGTH_SHORT)
                ivSelectedImage.hide()
                btnUpload.disable()
            }
        }
    }

    private fun showPermissionDeniedMessage(messageResId: Int) {
        binding.root.showSnackbar(getString(messageResId), Snackbar.LENGTH_SHORT)
    }

    private fun handleImageSelected(uri: Uri) {
        viewModel.onImageUriSelected(uri.toString())
    }

    private fun createImageUri(): Uri {
        val context = requireContext()
        val imageFile = File(
            context.filesDir,
            ImageConstants.generateFileName(ImageConstants.CAMERA_IMAGE_PREFIX)
        )
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            imageFile
        )
    }
}
