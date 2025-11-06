package model

import kotlinx.datetime.Instant

data class essage(
    val id: Long,
    val text: String,
    val timestamp: Instant,
    val onLeft: Boolean,
    val formattedTime: String
)
