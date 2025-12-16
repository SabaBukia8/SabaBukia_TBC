package com.example.mtgcollectionmanager.presentation.common

import android.widget.ImageView
import coil3.load
import coil3.request.crossfade


fun ImageView.loadImage(
    url: String,
    isCircle: Boolean = false,
    cornerRadius: Float = 0f
) {
    load(url) {
        crossfade(true)
    }
}
