package com.example.sababukia_tbc.data.repository

import android.annotation.SuppressLint
import android.content.Context
import com.example.sababukia_tbc.data.common.safeDatabaseCall
import com.example.sababukia_tbc.data.mapper.toDomain
import com.example.sababukia_tbc.data.mapper.toEntity
import com.example.sababukia_tbc.data.mapper.toUserLocation
import com.example.sababukia_tbc.data.remote.RemoteDataSource
import com.example.sababukia_tbc.domain.common.DomainError
import com.example.sababukia_tbc.domain.common.Resource
import com.example.sababukia_tbc.domain.model.Location
import com.example.sababukia_tbc.domain.model.UserLocation
import com.example.sababukia_tbc.domain.repository.LocationRepository
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject

class LocationRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val fusedLocationClient: FusedLocationProviderClient,
    private val localDataSource: com.example.sababukia_tbc.data.local.LocalDataSource,
    private val remoteDataSource: RemoteDataSource
) : LocationRepository {

    override suspend fun getLocations(): Flow<Resource<List<Location>>> = flow {
        emit(Resource.Loading)

        try {
            val cachedData = localDataSource.getAllLocations().first()
            if (cachedData.isNotEmpty()) {
                emit(Resource.Success(cachedData.map { it.toDomain() }))
            }
        } catch (e: Exception) {
        }

        try {
            val remoteLocations = remoteDataSource.getLocations()

            localDataSource.deleteAllLocations()
            localDataSource.insertLocations(remoteLocations.map { it.toEntity() })

            emit(Resource.Success(remoteLocations.map { it.toDomain() }))
        } catch (e: Exception) {
            val cachedData = localDataSource.getAllLocations().first()
            if (cachedData.isEmpty()) {
                emit(Resource.Error(DomainError.NetworkError))
            }
        }
    }

    override suspend fun getLocationById(id: String): Flow<Resource<Location?>> = flow {
        emit(Resource.Loading)
        val result = safeDatabaseCall {
            localDataSource.getLocationById(id)?.toDomain()
        }
        emit(result)
    }

    override suspend fun saveLocation(location: Location): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading)
        val locationToSave = if (location.id.isEmpty()) {
            location.copy(id = UUID.randomUUID().toString())
        } else {
            location
        }
        val result = safeDatabaseCall {
            localDataSource.insertLocation(locationToSave.toEntity())
        }
        emit(
            when (result) {
                is Resource.Success -> Resource.Success(Unit)
                is Resource.Error -> result
                is Resource.Loading -> Resource.Loading
            }
        )
    }

    override suspend fun deleteLocation(location: Location): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading)
        val result = safeDatabaseCall {
            localDataSource.deleteLocation(location.toEntity())
        }
        emit(
            when (result) {
                is Resource.Success -> Resource.Success(Unit)
                is Resource.Error -> result
                is Resource.Loading -> Resource.Loading
            }
        )
    }

    @SuppressLint("MissingPermission")
    override suspend fun getCurrentUserLocation(): Flow<Resource<UserLocation?>> = callbackFlow {
        trySend(Resource.Loading)

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                trySend(Resource.Success(location.toUserLocation()))
                close()
            } else {
                val locationRequest = LocationRequest.Builder(
                    Priority.PRIORITY_HIGH_ACCURACY,
                    10000L
                ).setMaxUpdates(1).build()

                val locationCallback = object : LocationCallback() {
                    override fun onLocationResult(result: LocationResult) {
                        result.lastLocation?.let {
                            trySend(Resource.Success(it.toUserLocation()))
                        } ?: trySend(Resource.Error(DomainError.LocationServicesUnavailable))
                        close()
                    }

                    override fun onLocationAvailability(availability: com.google.android.gms.location.LocationAvailability) {
                        if (!availability.isLocationAvailable) {
                            trySend(Resource.Error(DomainError.LocationServicesUnavailable))
                            close()
                        }
                    }
                }

                fusedLocationClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    context.mainLooper
                )
            }
        }.addOnFailureListener { exception ->
            trySend(Resource.Error(DomainError.LocationServicesUnavailable))
            close(exception)
        }

        awaitClose {}
    }.catch { e ->
        emit(Resource.Error(DomainError.UnknownError))
    }
}