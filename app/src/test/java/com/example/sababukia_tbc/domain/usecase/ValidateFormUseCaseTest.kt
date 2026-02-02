package com.example.sababukia_tbc.domain.usecase

import com.example.sababukia_tbc.domain.model.FieldType
import com.example.sababukia_tbc.domain.model.FormField
import com.example.sababukia_tbc.domain.model.FormSection
import com.example.sababukia_tbc.domain.model.KeyboardType
import com.example.sababukia_tbc.domain.model.RegistrationError
import com.example.sababukia_tbc.domain.model.Result
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ValidateFormUseCaseTest {

    private lateinit var useCase: ValidateFormUseCase

    private fun createField(
        id: Int,
        hint: String,
        isRequired: Boolean = false,
        isActive: Boolean = true
    ) = FormField(
        id = id,
        hint = hint,
        fieldType = FieldType.INPUT,
        keyboardType = KeyboardType.TEXT,
        isRequired = isRequired,
        isActive = isActive,
        iconUrl = null
    )

    @Before
    fun setUp() {
        useCase = ValidateFormUseCase()
    }

    @Test
    fun `invoke returns success when all required fields are filled`() {
        val sections = listOf(
            FormSection(
                id = 0,
                fields = listOf(
                    createField(1, "UserName", isRequired = true),
                    createField(2, "Email", isRequired = true)
                )
            )
        )
        val fieldValues = mapOf(1 to "john", 2 to "john@example.com")

        val result = useCase(sections, fieldValues)

        assertTrue(result is Result.Success)
    }

    @Test
    fun `invoke returns validation failed when required field is empty`() {
        val sections = listOf(
            FormSection(
                id = 0,
                fields = listOf(
                    createField(1, "UserName", isRequired = true),
                    createField(2, "Email", isRequired = true)
                )
            )
        )
        val fieldValues = mapOf(1 to "john", 2 to "")

        val result = useCase(sections, fieldValues)

        assertTrue(result is Result.Error)
        assertEquals(RegistrationError.ValidationFailed, (result as Result.Error).error)
    }

    @Test
    fun `invoke returns validation failed when required field is missing`() {
        val sections = listOf(
            FormSection(
                id = 0,
                fields = listOf(
                    createField(1, "UserName", isRequired = true),
                    createField(2, "Email", isRequired = true)
                )
            )
        )
        val fieldValues = mapOf(1 to "john")

        val result = useCase(sections, fieldValues)

        assertTrue(result is Result.Error)
        assertEquals(RegistrationError.ValidationFailed, (result as Result.Error).error)
    }

    @Test
    fun `invoke validates all required fields regardless of isActive flag`() {
        val sections = listOf(
            FormSection(
                id = 0,
                fields = listOf(
                    createField(1, "UserName", isRequired = true, isActive = true),
                    createField(2, "Email", isRequired = true, isActive = true)
                )
            )
        )
        val fieldValues = mapOf(1 to "john", 2 to "john@example.com")

        val result = useCase(sections, fieldValues)

        assertTrue(result is Result.Success)
    }

    @Test
    fun `invoke returns success when optional fields are empty`() {
        val sections = listOf(
            FormSection(
                id = 0,
                fields = listOf(
                    createField(1, "UserName", isRequired = true),
                    createField(2, "Nickname", isRequired = false)
                )
            )
        )
        val fieldValues = mapOf(1 to "john")

        val result = useCase(sections, fieldValues)

        assertTrue(result is Result.Success)
    }

    @Test
    fun `invoke returns pin mismatch when pins do not match`() {
        val sections = listOf(
            FormSection(
                id = 0,
                fields = listOf(
                    createField(1, "PIN", isRequired = true),
                    createField(2, "Confirm PIN", isRequired = true)
                )
            )
        )
        val fieldValues = mapOf(1 to "1234", 2 to "5678")

        val result = useCase(sections, fieldValues)

        assertTrue(result is Result.Error)
        assertEquals(RegistrationError.PinMismatch, (result as Result.Error).error)
    }

    @Test
    fun `invoke returns success when pins match`() {
        val sections = listOf(
            FormSection(
                id = 0,
                fields = listOf(
                    createField(1, "PIN", isRequired = true),
                    createField(2, "Confirm PIN", isRequired = true)
                )
            )
        )
        val fieldValues = mapOf(1 to "1234", 2 to "1234")

        val result = useCase(sections, fieldValues)

        assertTrue(result is Result.Success)
    }

    @Test
    fun `invoke handles case insensitive pin field detection`() {
        val sections = listOf(
            FormSection(
                id = 0,
                fields = listOf(
                    createField(1, "Enter your PIN", isRequired = true),
                    createField(2, "CONFIRM your PIN", isRequired = true)
                )
            )
        )
        val fieldValues = mapOf(1 to "1234", 2 to "5678")

        val result = useCase(sections, fieldValues)

        assertTrue(result is Result.Error)
        assertEquals(RegistrationError.PinMismatch, (result as Result.Error).error)
    }

    @Test
    fun `getFieldErrors returns errors for empty required fields`() {
        val sections = listOf(
            FormSection(
                id = 0,
                fields = listOf(
                    createField(1, "UserName", isRequired = true),
                    createField(2, "Email", isRequired = true)
                )
            )
        )
        val fieldValues = mapOf(1 to "john", 2 to "")

        val errors = useCase.getFieldErrors(sections, fieldValues)

        assertEquals(1, errors.size)
        assertEquals("Field is not filled (Email)", errors[2])
    }

    @Test
    fun `getFieldErrors returns error for pin mismatch`() {
        val sections = listOf(
            FormSection(
                id = 0,
                fields = listOf(
                    createField(1, "PIN", isRequired = true),
                    createField(2, "Confirm PIN", isRequired = true)
                )
            )
        )
        val fieldValues = mapOf(1 to "1234", 2 to "5678")

        val errors = useCase.getFieldErrors(sections, fieldValues)

        assertEquals(1, errors.size)
        assertEquals("PINs do not match", errors[2])
    }

    @Test
    fun `getFieldErrors returns empty map when all valid`() {
        val sections = listOf(
            FormSection(
                id = 0,
                fields = listOf(
                    createField(1, "UserName", isRequired = true),
                    createField(2, "Email", isRequired = true)
                )
            )
        )
        val fieldValues = mapOf(1 to "john", 2 to "john@example.com")

        val errors = useCase.getFieldErrors(sections, fieldValues)

        assertTrue(errors.isEmpty())
    }

    @Test
    fun `getFieldErrors validates all required fields regardless of isActive flag`() {
        // Note: isActive filtering is now handled by GetFormConfigurationUseCase
        // ValidateFormUseCase assumes all fields passed to it are active
        val sections = listOf(
            FormSection(
                id = 0,
                fields = listOf(
                    createField(1, "UserName", isRequired = true, isActive = true),
                    createField(2, "Email", isRequired = true, isActive = true)
                )
            )
        )
        val fieldValues = mapOf(1 to "john", 2 to "john@example.com")

        val errors = useCase.getFieldErrors(sections, fieldValues)

        assertTrue(errors.isEmpty())
    }

    @Test
    fun `invoke ignores pin validation when one pin is empty`() {
        val sections = listOf(
            FormSection(
                id = 0,
                fields = listOf(
                    createField(1, "PIN", isRequired = false),
                    createField(2, "Confirm PIN", isRequired = false)
                )
            )
        )
        val fieldValues = mapOf(1 to "1234", 2 to "")

        val result = useCase(sections, fieldValues)

        assertTrue(result is Result.Success)
    }
}
