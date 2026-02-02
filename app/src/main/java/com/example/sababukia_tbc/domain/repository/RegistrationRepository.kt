package com.example.sababukia_tbc.domain.repository

import com.example.sababukia_tbc.domain.model.FormSection
import com.example.sababukia_tbc.domain.model.RegistrationError
import com.example.sababukia_tbc.domain.model.Result

interface RegistrationRepository {
    suspend fun getFormConfiguration(): Result<List<FormSection>, RegistrationError>
}
