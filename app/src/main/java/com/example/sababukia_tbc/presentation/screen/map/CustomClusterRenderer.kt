package com.example.sababukia_tbc.presentation.screen.map

import android.content.Context
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer

class CustomClusterRenderer(
    private val context: Context,
    map: GoogleMap,
    clusterManager: ClusterManager<LocationClusterItem>
) : DefaultClusterRenderer<LocationClusterItem>(context, map, clusterManager) {

    override fun onBeforeClusterItemRendered(
        item: LocationClusterItem,
        markerOptions: MarkerOptions
    ) {
        markerOptions
            .title(item.title)
            .snippet(item.snippet)
            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
    }

    override fun shouldRenderAsCluster(cluster: Cluster<LocationClusterItem>): Boolean {
        // Cluster when 2 or more items are close together
        return cluster.size > 1
    }
}