package com.example.sababukia_tbc.data.local

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocalDataSource @Inject constructor(
    private val locationDao: LocationDao
) {
    fun getAllLocations(): Flow<List<LocationEntity>> {
        return locationDao.getAllLocations()
    }

    suspend fun getLocationById(id: String): LocationEntity? {
        return locationDao.getLocationById(id)
    }

    suspend fun insertLocation(location: LocationEntity) {
        locationDao.insertLocation(location)
    }

    suspend fun insertLocations(locations: List<LocationEntity>) {
        locations.forEach { locationDao.insertLocation(it) }
    }

    suspend fun deleteLocation(location: LocationEntity) {
        locationDao.deleteLocation(location)
    }

    suspend fun deleteAllLocations() {
        locationDao.deleteAllLocations()
    }
}
