package com.example.sababukia_tbc.presentation.screen.statistics

import android.view.View
import androidx.viewpager2.widget.ViewPager2
import kotlin.math.abs

class CardPeekTransformer : ViewPager2.PageTransformer {
    override fun transformPage(page: View, position: Float) {
        page.apply {
            val absPosition = abs(position)

            when {
                position < -1 || position > 1 -> {
                    alpha = 0f
                }
                else -> {
                    alpha = 1f - (absPosition * 0.3f)
                    scaleY = 1f - (absPosition * 0.1f)
                }
            }
        }
    }
}
