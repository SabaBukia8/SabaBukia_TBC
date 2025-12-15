package com.example.sababukia_tbc.data.mapper

import android.location.Location as AndroidLocation
import com.example.sababukia_tbc.data.local.LocationEntity
import com.example.sababukia_tbc.data.model.remote.LocationDto
import com.example.sababukia_tbc.domain.model.Location
import com.example.sababukia_tbc.domain.model.UserLocation

fun AndroidLocation?.toUserLocation(): UserLocation? {
    if (this == null) return null
    return UserLocation(
        latitude = this.latitude,
        longitude = this.longitude,
        altitude = if (this.hasAltitude()) this.altitude else null,
        accuracy = if (this.hasAccuracy()) this.accuracy else null,
        speed = if (this.hasSpeed()) this.speed else null,
        bearing = if (this.hasBearing()) this.bearing else null,
        provider = this.provider,
        time = this.time
    )
}

// DTO to Domain
fun LocationDto.toDomain(): Location {
    return Location(
        id = id.toString(),
        name = title,
        latitude = latitude,
        longitude = longitude,
        description = description
    )
}

// DTO to Entity
fun LocationDto.toEntity(): LocationEntity {
    return LocationEntity(
        id = id.toString(),
        name = title,
        latitude = latitude,
        longitude = longitude,
        description = description
    )
}

// Entity to Domain
fun LocationEntity.toDomain(): Location {
    return Location(
        id = id,
        name = name,
        latitude = latitude,
        longitude = longitude,
        description = description
    )
}

// Domain to Entity
fun Location.toEntity(): LocationEntity {
    return LocationEntity(
        id = id,
        name = name,
        latitude = latitude,
        longitude = longitude,
        description = description
    )
}