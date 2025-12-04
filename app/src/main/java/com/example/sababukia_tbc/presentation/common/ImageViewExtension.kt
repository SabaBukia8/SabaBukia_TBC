package com.example.sababukia_tbc.presentation.common

import android.widget.ImageView
import coil3.load
import coil3.request.crossfade
import coil3.request.error
import coil3.request.placeholder
import coil3.request.transformations
import coil3.transform.CircleCropTransformation
import com.example.sababukia_tbc.R

fun ImageView.loadImage(
    url: String?,
    placeholderRes: Int = R.drawable.ic_launcher_foreground,
    errorRes: Int = R.drawable.ic_launcher_foreground,
    enableCircleCrop: Boolean = true
) {
    load(url) {
        crossfade(true)
        placeholder(placeholderRes)
        error(errorRes)
        if (enableCircleCrop) {
            transformations(CircleCropTransformation())
        }
    }
}
