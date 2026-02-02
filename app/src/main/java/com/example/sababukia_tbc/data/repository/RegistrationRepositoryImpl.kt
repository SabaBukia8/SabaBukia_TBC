package com.example.sababukia_tbc.data.repository

import com.example.sababukia_tbc.data.common.registrationSafeCall
import com.example.sababukia_tbc.data.remote.api.RegistrationApiService
import com.example.sababukia_tbc.data.remote.mapper.toFormSections
import com.example.sababukia_tbc.domain.model.FormSection
import com.example.sababukia_tbc.domain.model.RegistrationError
import com.example.sababukia_tbc.domain.model.Result
import com.example.sababukia_tbc.domain.repository.RegistrationRepository
import javax.inject.Inject

class RegistrationRepositoryImpl @Inject constructor(
    private val apiService: RegistrationApiService
) : RegistrationRepository {

    override suspend fun getFormConfiguration(): Result<List<FormSection>, RegistrationError> {
        return registrationSafeCall {
            apiService.getFormConfiguration().toFormSections()
        }
    }
}
