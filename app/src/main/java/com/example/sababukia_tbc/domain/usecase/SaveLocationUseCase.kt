package com.example.sababukia_tbc.domain.usecase

import com.example.sababukia_tbc.domain.common.DomainError
import com.example.sababukia_tbc.domain.common.Resource
import com.example.sababukia_tbc.domain.model.Location
import com.example.sababukia_tbc.domain.repository.LocationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import javax.inject.Inject

class SaveLocationUseCase @Inject constructor(
    private val repository: LocationRepository
) {
    suspend operator fun invoke(location: Location): Flow<Resource<Unit>> {
        return repository.saveLocation(location)
            .catch { e -> emit(Resource.Error(
                DomainError.GeneralError(
                    Throwable(e.message ?: "Unknown error occurred")
                ))) }
    }
}