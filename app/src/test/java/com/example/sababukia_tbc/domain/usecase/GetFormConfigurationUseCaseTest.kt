package com.example.sababukia_tbc.domain.usecase

import com.example.sababukia_tbc.domain.model.FieldType
import com.example.sababukia_tbc.domain.model.FormField
import com.example.sababukia_tbc.domain.model.FormSection
import com.example.sababukia_tbc.domain.model.KeyboardType
import com.example.sababukia_tbc.domain.model.RegistrationError
import com.example.sababukia_tbc.domain.model.Result
import com.example.sababukia_tbc.domain.repository.RegistrationRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GetFormConfigurationUseCaseTest {

    private lateinit var repository: RegistrationRepository
    private lateinit var useCase: GetFormConfigurationUseCase

    @Before
    fun setUp() {
        repository = mockk()
        useCase = GetFormConfigurationUseCase(repository)
    }

    @Test
    fun `invoke returns success when repository returns form configuration`() = runTest {
        val sections = listOf(
            FormSection(
                id = 0,
                fields = listOf(
                    FormField(
                        id = 1,
                        hint = "UserName",
                        fieldType = FieldType.INPUT,
                        keyboardType = KeyboardType.TEXT,
                        isRequired = true,
                        isActive = true,
                        iconUrl = null
                    )
                )
            )
        )
        coEvery { repository.getFormConfiguration() } returns Result.Success(sections)

        val result = useCase()

        assertTrue(result is Result.Success)
        assertEquals(sections, (result as Result.Success).data)
    }

    @Test
    fun `invoke returns error when repository returns network error`() = runTest {
        coEvery { repository.getFormConfiguration() } returns Result.Error(RegistrationError.Network)

        val result = useCase()

        assertTrue(result is Result.Error)
        assertEquals(RegistrationError.Network, (result as Result.Error).error)
    }

    @Test
    fun `invoke returns error when repository returns unknown error`() = runTest {
        val errorMessage = "Something went wrong"
        coEvery { repository.getFormConfiguration() } returns Result.Error(RegistrationError.Unknown(errorMessage))

        val result = useCase()

        assertTrue(result is Result.Error)
        val error = (result as Result.Error).error as RegistrationError.Unknown
        assertEquals(errorMessage, error.message)
    }
}
