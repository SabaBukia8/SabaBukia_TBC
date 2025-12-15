package com.example.sababukia_tbc.presentation.screen.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.sababukia_tbc.databinding.BottomSheetLocationDetailsBinding
import com.example.sababukia_tbc.presentation.model.LocationUiModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class LocationDetailsBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomSheetLocationDetailsBinding? = null
    private val binding get() = _binding!!

    private val locationName: String by lazy {
        arguments?.getString(ARG_NAME) ?: ""
    }

    private val locationDescription: String by lazy {
        arguments?.getString(ARG_DESCRIPTION) ?: ""
    }

    private val latitude: Double by lazy {
        arguments?.getDouble(ARG_LATITUDE) ?: 0.0
    }

    private val longitude: Double by lazy {
        arguments?.getDouble(ARG_LONGITUDE) ?: 0.0
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetLocationDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
    }

    private fun setupViews() {
        with(binding) {
            tvName.text = locationName
            tvDescription.text = locationDescription.ifEmpty { "N/A" }
            tvLatitude.text = String.format("%.6f", latitude)
            tvLongitude.text = String.format("%.6f", longitude)

            btnClose.setOnClickListener {
                dismiss()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_NAME = "name"
        private const val ARG_DESCRIPTION = "description"
        private const val ARG_LATITUDE = "latitude"
        private const val ARG_LONGITUDE = "longitude"

        fun newInstance(location: LocationUiModel): LocationDetailsBottomSheet {
            return LocationDetailsBottomSheet().apply {
                arguments = Bundle().apply {
                    putString(ARG_NAME, location.name)
                    putString(ARG_DESCRIPTION, location.description)
                    putDouble(ARG_LATITUDE, location.latLng.latitude)
                    putDouble(ARG_LONGITUDE, location.latLng.longitude)
                }
            }
        }
    }
}