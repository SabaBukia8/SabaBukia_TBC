package com.example.sababukia_tbc.domain.repository

import com.example.sababukia_tbc.domain.common.Resource
import com.example.sababukia_tbc.domain.model.Location
import com.example.sababukia_tbc.domain.model.UserLocation
import kotlinx.coroutines.flow.Flow

interface LocationRepository {
    suspend fun getLocations(): Flow<Resource<List<Location>>>
    suspend fun getLocationById(id: String): Flow<Resource<Location?>>
    suspend fun saveLocation(location: Location): Flow<Resource<Unit>>
    suspend fun deleteLocation(location: Location): Flow<Resource<Unit>>
    suspend fun getCurrentUserLocation(): Flow<Resource<UserLocation?>>
}