package model

import kotlinx.datetime.Instant

data class Message(
    val id: Long,
    val text: String,
    val timestamp: Instant,
    val onLeft: Boolean
)
