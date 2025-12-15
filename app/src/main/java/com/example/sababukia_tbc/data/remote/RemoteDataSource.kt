package com.example.sababukia_tbc.data.remote

import com.example.sababukia_tbc.data.model.remote.LocationDto
import com.example.sababukia_tbc.data.remote.api.LocationApiService
import javax.inject.Inject

class RemoteDataSource @Inject constructor(
    private val locationApiService: LocationApiService
) {
    suspend fun getLocations(): List<LocationDto> {
        val response = locationApiService.getLocations()
        if (response.isSuccessful) {
            return response.body() ?: emptyList()
        } else {
            throw Exception("Failed to fetch locations: ${response.code()}")
        }
    }
}
