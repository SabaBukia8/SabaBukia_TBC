package com.example.sababukia_tbc.data.repository

import com.example.sababukia_tbc.data.common.safeCall
import com.example.sababukia_tbc.data.mapper.toDomainCategories
import com.example.sababukia_tbc.data.mapper.toDomainEvents
import com.example.sababukia_tbc.data.remote.StoreApi
import com.example.sababukia_tbc.domain.model.Category
import com.example.sababukia_tbc.domain.model.Event
import com.example.sababukia_tbc.domain.model.Result
import com.example.sababukia_tbc.domain.model.StoreError
import com.example.sababukia_tbc.domain.repository.StoreRepository
import java.io.IOException
import javax.inject.Inject

class StoreRepositoryImpl @Inject constructor(
    private val api: StoreApi
) : StoreRepository {

    override suspend fun getEvents(): Result<List<Event>, StoreError> = safeCall(
        exceptionMapper = ::mapException
    ) {
        api.getEvents().toDomainEvents()
    }

    override suspend fun getCategories(): Result<List<Category>, StoreError> = safeCall(
        exceptionMapper = ::mapException
    ) {
        api.getCategories().toDomainCategories()
    }

    private fun mapException(e: Exception): StoreError = when (e) {
        is IOException -> StoreError.NetworkError
        else -> StoreError.Unknown(e)
    }
}
