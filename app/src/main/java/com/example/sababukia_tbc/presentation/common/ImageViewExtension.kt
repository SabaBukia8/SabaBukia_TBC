package com.example.sababukia_tbc.presentation.common

import android.widget.ImageView
import coil3.load
import coil3.request.crossfade
import coil3.request.transformations
import coil3.transform.CircleCropTransformation
import coil3.transform.RoundedCornersTransformation

fun ImageView.loadImage(
    url: String,
    isCircle: Boolean = false,
    cornerRadius: Float = 0f
) {
    load(url) {
        crossfade(true)
        when {
            isCircle -> transformations(CircleCropTransformation())
            cornerRadius > 0 -> transformations(RoundedCornersTransformation(cornerRadius))
        }
    }
}
