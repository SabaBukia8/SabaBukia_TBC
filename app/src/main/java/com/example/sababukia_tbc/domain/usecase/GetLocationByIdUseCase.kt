package com.example.sababukia_tbc.domain.usecase

import com.example.sababukia_tbc.domain.common.DomainError
import com.example.sababukia_tbc.domain.common.Resource
import com.example.sababukia_tbc.domain.model.Location
import com.example.sababukia_tbc.domain.repository.LocationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetLocationByIdUseCase @Inject constructor(
    private val repository: LocationRepository
) {
    suspend operator fun invoke(id: String): Flow<Resource<Location?>> {
        return repository.getLocationById(id)
            .catch { e -> emit(Resource.Error(
                DomainError.GeneralError(
                    Throwable(e.message ?: "Unknown error occurred")
                ))) }
    }
}