package com.example.sababukia_tbc.presentation.mapper

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateFormatter {

    private val displayFormat = SimpleDateFormat("MMM dd, yyyy", Locale.US)

    fun formatEpochToDate(epochSeconds: Long): String {
        val date = Date(epochSeconds * 1000)
        return displayFormat.format(date)
    }
}
