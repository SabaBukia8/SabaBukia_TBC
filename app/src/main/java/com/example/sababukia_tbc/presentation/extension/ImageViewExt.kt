package com.example.sababukia_tbc.presentation.extension

import android.widget.ImageView
import coil.load
import coil.transform.CircleCropTransformation
import coil.transform.RoundedCornersTransformation
import com.example.sababukia_tbc.R

fun ImageView.loadImage(
    url: String?,
    placeholder: Int = R.drawable.ic_person,
    error: Int = R.drawable.ic_person,
    isCircular: Boolean = false
) {
    load(url) {
        placeholder(placeholder)
        error(error)
        crossfade(true)
        if (isCircular) {
            transformations(CircleCropTransformation())
        }
    }
}

fun ImageView.loadCircularImage(
    url: String?,
    placeholder: Int = R.drawable.ic_person,
    error: Int = R.drawable.ic_person
) {
    load(url) {
        placeholder(placeholder)
        error(error)
        crossfade(true)
        transformations(CircleCropTransformation())
    }
}

fun ImageView.loadRoundedImage(
    url: String?,
    cornerRadius: Float = 8f,
    placeholder: Int = R.drawable.ic_person,
    error: Int = R.drawable.ic_person
) {
    load(url) {
        placeholder(placeholder)
        error(error)
        crossfade(true)
        transformations(RoundedCornersTransformation(cornerRadius))
    }
}
