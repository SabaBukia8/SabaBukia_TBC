package com.example.sababukia_tbc.presentation.mapper

import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

object DateFormatter {

    fun formatEpochToDate(epochSeconds: Long): String {
        val instant = Instant.fromEpochSeconds(epochSeconds)
        val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
        val month = localDateTime.month.name.take(3).lowercase().replaceFirstChar { it.uppercase() }
        val day = localDateTime.dayOfMonth.toString().padStart(2, '0')
        val year = localDateTime.year
        return "$month $day, $year"
    }
}
