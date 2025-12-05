package com.example.sababukia_tbc.data.mapper

import com.example.sababukia_tbc.data.model.remote.WorkspaceDto
import com.example.sababukia_tbc.domain.model.WorkspaceItem
import kotlin.random.Random

fun WorkspaceDto.toDomain(): WorkspaceItem = WorkspaceItem(
    location = location,
    altitudeM = altitudeM,
    title = title,
    image = image,
    stars = stars,
    price = calculatePrice(altitudeM, stars)
)

private fun calculatePrice(altitude: Int, stars: Int): Int {
    val basePrice = 50
    val altitudeBonus = (altitude / 100) * 10
    val starsBonus = stars * 20
    return basePrice + altitudeBonus + starsBonus + Random.nextInt(-20, 30)
}
