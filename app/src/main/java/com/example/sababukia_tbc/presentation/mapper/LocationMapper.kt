package com.example.sababukia_tbc.presentation.mapper

import com.example.sababukia_tbc.domain.model.Location
import com.example.sababukia_tbc.domain.model.UserLocation
import com.example.sababukia_tbc.presentation.model.LocationUiModel
import com.google.android.gms.maps.model.LatLng


fun Location.toLocationUiModel(): LocationUiModel {
    return LocationUiModel(
        id = this.id,
        name = this.name,
        description = this.description ?: "",
        latLng = LatLng(this.latitude, this.longitude)
    )
}


fun LocationUiModel.toLocation(): Location {
    return Location(
        id = this.id,
        name = this.name,
        description = this.description,
        latitude = this.latLng.latitude,
        longitude = this.latLng.longitude
    )
}


fun UserLocation.toLatLng(): LatLng {
    return LatLng(this.latitude, this.longitude)
}