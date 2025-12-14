package com.example.mtgcollectionmanager.presentation.common

import android.content.Context
import android.widget.ImageView
import android.widget.LinearLayout
import com.example.mtgcollectionmanager.R

object ManaSymbolRenderer {

    fun renderManaSymbols(
        context: Context,
        manaCost: String?,
        container: LinearLayout
    ) {
        container.removeAllViews()

        if (manaCost.isNullOrBlank()) return

        // Parse mana cost string into individual symbols
        val symbols = parseManaString(manaCost)

        symbols.forEach { symbol ->
            val imageView = createManaSymbolView(context, symbol)
            container.addView(imageView)
        }
    }

    private fun parseManaString(mana: String): List<String> {
        val regex = """\{([^}]+)\}""".toRegex()
        return regex.findAll(mana).map { it.groupValues[1] }.toList()
    }

    private fun createManaSymbolView(context: Context, symbol: String): ImageView {
        val size = context.resources.getDimensionPixelSize(R.dimen.mana_symbol_size)
        val spacing = context.resources.getDimensionPixelSize(R.dimen.mana_symbol_spacing)

        return ImageView(context).apply {
            layoutParams = LinearLayout.LayoutParams(size, size).apply {
                marginEnd = spacing
            }
            setImageResource(getManaIcon(symbol))
            scaleType = ImageView.ScaleType.FIT_CENTER
            contentDescription = "Mana symbol: $symbol"
        }
    }

    private fun getManaIcon(symbol: String): Int {
        return when (symbol.uppercase()) {
            "W" -> R.drawable.ic_mana_white
            "U" -> R.drawable.ic_mana_blue
            "B" -> R.drawable.ic_mana_black
            "R" -> R.drawable.ic_mana_red
            "G" -> R.drawable.ic_mana_green
            else -> R.drawable.ic_mana_colorless
        }
    }


    fun renderLargeManaSymbols(
        context: Context,
        manaCost: String?,
        container: LinearLayout
    ) {
        container.removeAllViews()

        if (manaCost.isNullOrBlank()) return

        val symbols = parseManaString(manaCost)
        val size = context.resources.getDimensionPixelSize(R.dimen.mana_symbol_size_lg)
        val spacing = context.resources.getDimensionPixelSize(R.dimen.mana_symbol_spacing)

        symbols.forEach { symbol ->
            val imageView = ImageView(context).apply {
                layoutParams = LinearLayout.LayoutParams(size, size).apply {
                    marginEnd = spacing
                }
                setImageResource(getManaIcon(symbol))
                scaleType = ImageView.ScaleType.FIT_CENTER
                contentDescription = "Mana symbol: $symbol"
            }
            container.addView(imageView)
        }
    }
}
