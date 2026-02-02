package com.example.sababukia_tbc.domain.usecase

import com.example.sababukia_tbc.domain.model.FormSection
import com.example.sababukia_tbc.domain.model.RegistrationError
import com.example.sababukia_tbc.domain.model.Result
import com.example.sababukia_tbc.domain.repository.RegistrationRepository
import javax.inject.Inject

class GetFormConfigurationUseCase @Inject constructor(
    private val repository: RegistrationRepository
) {
    suspend operator fun invoke(): Result<List<FormSection>, RegistrationError> {
        return when (val result = repository.getFormConfiguration()) {
            is Result.Success -> Result.Success(
                result.data.map { section ->
                    section.copy(fields = section.fields.filter { it.isActive })
                }
            )
            is Result.Error -> result
        }
    }
}
