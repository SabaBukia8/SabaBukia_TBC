package com.example.mtgcollectionmanager.presentation.common

import android.content.Context
import android.widget.ImageView
import androidx.appcompat.widget.LinearLayoutCompat
import com.example.mtgcollectionmanager.R

object ManaSymbolRenderer {

    fun renderManaSymbols(
        context: Context,
        manaCost: String?,
        container: LinearLayoutCompat
    ) {
        container.removeAllViews()

        if (manaCost.isNullOrBlank()) return


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
            layoutParams = LinearLayoutCompat.LayoutParams(size, size).apply {
                marginEnd = spacing
            }
            setImageResource(getManaIcon(symbol))
            scaleType = ImageView.ScaleType.FIT_CENTER
            contentDescription = "Mana symbol: $symbol"
        }
    }

    private fun getManaIcon(symbol: String): Int {
        val upperSymbol = symbol.uppercase()


        if (upperSymbol.contains("/")) {
            return getHybridManaIcon(upperSymbol)
        }

        return when (upperSymbol) {

            "W" -> R.drawable.ic_mana_white
            "U" -> R.drawable.ic_mana_blue
            "B" -> R.drawable.ic_mana_black
            "R" -> R.drawable.ic_mana_red
            "G" -> R.drawable.ic_mana_green


            "X" -> R.drawable.ic_mana_x
            "C" -> R.drawable.ic_mana_c
            "S" -> R.drawable.ic_mana_snow


            "0" -> R.drawable.ic_mana_colorless  // 0 uses colorless
            "1" -> R.drawable.ic_mana_1
            "2" -> R.drawable.ic_mana_2
            "3" -> R.drawable.ic_mana_3
            "4" -> R.drawable.ic_mana_4
            "5" -> R.drawable.ic_mana_5
            "6" -> R.drawable.ic_mana_6
            "7" -> R.drawable.ic_mana_7
            "8" -> R.drawable.ic_mana_8
            "9" -> R.drawable.ic_mana_9
            "10" -> R.drawable.ic_mana_10
            "11" -> R.drawable.ic_mana_11
            "12" -> R.drawable.ic_mana_12
            "13" -> R.drawable.ic_mana_13
            "14" -> R.drawable.ic_mana_14
            "15" -> R.drawable.ic_mana_15
            "16" -> R.drawable.ic_mana_16
            "17" -> R.drawable.ic_mana_17
            "18" -> R.drawable.ic_mana_18
            "19" -> R.drawable.ic_mana_19
            "20" -> R.drawable.ic_mana_20


            else -> R.drawable.ic_mana_colorless
        }
    }

    private fun getHybridManaIcon(symbol: String): Int {

        if (symbol.endsWith("/P")) {
            val color = symbol.substringBefore("/")
            return when (color) {
                "W" -> R.drawable.ic_mana_phyrexian_w
                "U" -> R.drawable.ic_mana_phyrexian_u
                "B" -> R.drawable.ic_mana_phyrexian_b
                "R" -> R.drawable.ic_mana_phyrexian_r
                "G" -> R.drawable.ic_mana_phyrexian_g
                else -> R.drawable.ic_mana_colorless
            }
        }

        val parts = symbol.split("/")
        if (parts.size != 2) return R.drawable.ic_mana_colorless

        val first = parts[0]
        val second = parts[1]


        if (first == "2") {
            return when (second) {
                "W" -> R.drawable.ic_mana_2w
                "U" -> R.drawable.ic_mana_2u
                "B" -> R.drawable.ic_mana_2b
                "R" -> R.drawable.ic_mana_2r
                "G" -> R.drawable.ic_mana_2g
                else -> R.drawable.ic_mana_colorless
            }
        }


        val hybridKey = "${first}${second}".lowercase()

        return when (hybridKey) {
            "wu" -> R.drawable.ic_mana_wu
            "wb" -> R.drawable.ic_mana_wb
            "ub" -> R.drawable.ic_mana_ub
            "ur" -> R.drawable.ic_mana_ur
            "br" -> R.drawable.ic_mana_br
            "bg" -> R.drawable.ic_mana_bg
            "rg" -> R.drawable.ic_mana_rg
            "rw" -> R.drawable.ic_mana_rw
            "gw" -> R.drawable.ic_mana_gw
            "gu" -> R.drawable.ic_mana_gu

            "uw" -> R.drawable.ic_mana_wu
            "bw" -> R.drawable.ic_mana_wb
            "bu" -> R.drawable.ic_mana_ub
            "ru" -> R.drawable.ic_mana_ur
            "rb" -> R.drawable.ic_mana_br
            "gb" -> R.drawable.ic_mana_bg
            "gr" -> R.drawable.ic_mana_rg
            "wr" -> R.drawable.ic_mana_rw
            "wg" -> R.drawable.ic_mana_gw
            "ug" -> R.drawable.ic_mana_gu

            "cw" -> R.drawable.ic_mana_cw
            "cu" -> R.drawable.ic_mana_cu
            "cb" -> R.drawable.ic_mana_cb
            "cr" -> R.drawable.ic_mana_cr
            "cg" -> R.drawable.ic_mana_cg
            "wc" -> R.drawable.ic_mana_cw
            "uc" -> R.drawable.ic_mana_cu
            "bc" -> R.drawable.ic_mana_cb
            "rc" -> R.drawable.ic_mana_cr
            "gc" -> R.drawable.ic_mana_cg
            else -> R.drawable.ic_mana_colorless
        }
    }


    fun renderLargeManaSymbols(
        context: Context,
        manaCost: String?,
        container: LinearLayoutCompat
    ) {
        container.removeAllViews()

        if (manaCost.isNullOrBlank()) return

        val symbols = parseManaString(manaCost)
        val size = context.resources.getDimensionPixelSize(R.dimen.mana_symbol_size_lg)
        val spacing = context.resources.getDimensionPixelSize(R.dimen.mana_symbol_spacing)

        symbols.forEach { symbol ->
            val imageView = ImageView(context).apply {
                layoutParams = LinearLayoutCompat.LayoutParams(size, size).apply {
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
