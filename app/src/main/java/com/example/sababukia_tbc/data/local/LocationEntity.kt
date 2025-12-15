package com.example.sababukia_tbc.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.sababukia_tbc.domain.model.Location

@Entity(tableName = "locations")
data class LocationEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val description: String?
) {
    fun toLocation(): Location {
        return Location(
            id = id,
            name = name,
            latitude = latitude,
            longitude = longitude,
            description = description
        )
    }

    companion object {
        fun fromLocation(location: Location): LocationEntity {
            return LocationEntity(
                id = location.id,
                name = location.name,
                latitude = location.latitude,
                longitude = location.longitude,
                description = location.description
            )
        }
    }
}