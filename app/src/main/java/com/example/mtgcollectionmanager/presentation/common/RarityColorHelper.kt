package com.example.mtgcollectionmanager.presentation.common

import android.content.Context
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import com.example.mtgcollectionmanager.R

object RarityColorHelper {

    @ColorInt
    fun getRarityColor(context: Context, rarity: String?): Int {
        return when (rarity?.lowercase()) {
            "common" -> ContextCompat.getColor(context, R.color.rarity_common)
            "uncommon" -> ContextCompat.getColor(context, R.color.rarity_uncommon)
            "rare" -> ContextCompat.getColor(context, R.color.rarity_rare)
            "mythic", "mythic rare" -> ContextCompat.getColor(context, R.color.rarity_mythic)
            "special", "bonus" -> ContextCompat.getColor(context, R.color.rarity_special)
            else -> ContextCompat.getColor(context, R.color.outline) // Default border color
        }
    }

    fun getRarityColorRes(rarity: String?): Int {
        return when (rarity?.lowercase()) {
            "common" -> R.color.rarity_common
            "uncommon" -> R.color.rarity_uncommon
            "rare" -> R.color.rarity_rare
            "mythic", "mythic rare" -> R.color.rarity_mythic
            "special", "bonus" -> R.color.rarity_special
            else -> R.color.outline
        }
    }

    fun getRarityDisplayName(rarity: String?): String {
        return when (rarity?.lowercase()) {
            "common" -> "Common"
            "uncommon" -> "Uncommon"
            "rare" -> "Rare"
            "mythic", "mythic rare" -> "Mythic Rare"
            "special", "bonus" -> "Special"
            else -> "Unknown"
        }
    }

    fun isPremiumRarity(rarity: String?): Boolean {
        return when (rarity?.lowercase()) {
            "rare", "mythic", "mythic rare", "special", "bonus" -> true
            else -> false
        }
    }
}
