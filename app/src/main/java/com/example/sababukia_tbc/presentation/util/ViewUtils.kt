package com.example.sababukia_tbc.presentation.util

import android.view.View
import com.example.sababukia_tbc.presentation.common.hide
import com.example.sababukia_tbc.presentation.common.show

object ViewUtils {
    fun showViews(vararg views: View) {
        views.forEach { it.show() }
    }

    fun hideViews(vararg views: View) {
        views.forEach { it.hide() }
    }
}
