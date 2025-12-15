package com.example.sababukia_tbc.presentation.model

import com.google.android.gms.maps.model.LatLng

data class LocationUiModel(
    val id: String,
    val name: String,
    val description: String,
    val latLng: LatLng
) {
    val position: LatLng
        get() = latLng
}