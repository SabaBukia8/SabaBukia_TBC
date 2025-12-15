package com.example.sababukia_tbc.presentation.screen.map

import com.example.sababukia_tbc.presentation.model.LocationUiModel
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem

class LocationClusterItem(
    private val locationUiModel: LocationUiModel
) : ClusterItem {

    private val itemPosition: LatLng = locationUiModel.latLng
    private val itemTitle: String = locationUiModel.name
    private val itemSnippet: String = locationUiModel.description

    override fun getPosition(): LatLng = itemPosition

    override fun getTitle(): String = itemTitle

    override fun getSnippet(): String = itemSnippet

    fun getLocation(): LocationUiModel = locationUiModel
}