package com.example.sababukia_tbc.presentation.screen.imageupload

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.sababukia_tbc.databinding.BottomSheetImageSelectionBinding
import com.example.sababukia_tbc.presentation.extension.onClick
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ImageSelectionBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomSheetImageSelectionBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ImageUploadViewModel by viewModels({ requireParentFragment() })

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetImageSelectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListeners()
    }

    private fun setupListeners() {
        binding.apply {
            btnTakePicture.onClick {
                viewModel.onEvent(ImageUploadEvent.CameraSelected)
            }

            btnChooseFromGallery.onClick {
                viewModel.onEvent(ImageUploadEvent.GallerySelected)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
