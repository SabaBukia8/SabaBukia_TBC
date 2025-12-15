package com.example.sababukia_tbc.domain.usecase

import com.example.sababukia_tbc.domain.common.DomainError
import com.example.sababukia_tbc.domain.common.Resource
import com.example.sababukia_tbc.domain.model.Location
import com.example.sababukia_tbc.domain.repository.LocationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetLocationsUseCase @Inject constructor(
    private val repository: LocationRepository
) {
    suspend operator fun invoke(): Flow<Resource<List<Location>>> {
        return repository.getLocations()
            .catch { e -> emit(Resource.Error(
                DomainError.GeneralError(
                    Throwable(e.message ?: "Unknown error occurred")
                ))) }
    }
}