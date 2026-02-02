package com.example.sababukia_tbc.data.remote.mapper

import com.example.sababukia_tbc.data.remote.dto.FormFieldDto
import com.example.sababukia_tbc.domain.model.FieldType
import com.example.sababukia_tbc.domain.model.KeyboardType
import kotlinx.serialization.json.JsonPrimitive
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class FormFieldMapperTest {

    @Test
    fun `toFormField maps FormFieldDto to FormField correctly`() {
        val dto = FormFieldDto(
            fieldId = 1,
            hint = "UserName",
            fieldType = "input",
            keyboard = "text",
            required = JsonPrimitive(true),
            isActive = true,
            icon = "https://example.com/icon.png"
        )

        val result = dto.toFormField()

        assertEquals(1, result.id)
        assertEquals("UserName", result.hint)
        assertEquals(FieldType.INPUT, result.fieldType)
        assertEquals(KeyboardType.TEXT, result.keyboardType)
        assertTrue(result.isRequired)
        assertTrue(result.isActive)
        assertEquals("https://example.com/icon.png", result.iconUrl)
    }

    @Test
    fun `toFormField maps chooser field type correctly`() {
        val dto = FormFieldDto(
            fieldId = 2,
            hint = "Birthday",
            fieldType = "chooser",
            keyboard = null,
            required = JsonPrimitive(false),
            isActive = true,
            icon = null
        )

        val result = dto.toFormField()

        assertEquals(FieldType.CHOOSER, result.fieldType)
        assertNull(result.iconUrl)
    }

    @Test
    fun `toFormField maps number keyboard type correctly`() {
        val dto = FormFieldDto(
            fieldId = 3,
            hint = "PIN",
            fieldType = "input",
            keyboard = "number",
            required = JsonPrimitive(true),
            isActive = true,
            icon = null
        )

        val result = dto.toFormField()

        assertEquals(KeyboardType.NUMBER, result.keyboardType)
    }

    @Test
    fun `toFormField maps email keyboard type correctly`() {
        val dto = FormFieldDto(
            fieldId = 4,
            hint = "Email",
            fieldType = "input",
            keyboard = "email",
            required = JsonPrimitive(true),
            isActive = true,
            icon = null
        )

        val result = dto.toFormField()

        assertEquals(KeyboardType.EMAIL, result.keyboardType)
    }

    @Test
    fun `toFormField handles null keyboard as text`() {
        val dto = FormFieldDto(
            fieldId = 5,
            hint = "Name",
            fieldType = "input",
            keyboard = null,
            required = JsonPrimitive(false),
            isActive = true,
            icon = null
        )

        val result = dto.toFormField()

        assertEquals(KeyboardType.TEXT, result.keyboardType)
    }

    @Test
    fun `toFormField handles boolean true required correctly`() {
        val dto = FormFieldDto(
            fieldId = 6,
            hint = "Field",
            fieldType = "input",
            keyboard = "text",
            required = JsonPrimitive(true),
            isActive = true,
            icon = null
        )

        val result = dto.toFormField()

        assertTrue(result.isRequired)
    }

    @Test
    fun `toFormField handles boolean false required correctly`() {
        val dto = FormFieldDto(
            fieldId = 7,
            hint = "Field",
            fieldType = "input",
            keyboard = "text",
            required = JsonPrimitive(false),
            isActive = true,
            icon = null
        )

        val result = dto.toFormField()

        assertFalse(result.isRequired)
    }

    @Test
    fun `toFormField handles string true required correctly`() {
        val dto = FormFieldDto(
            fieldId = 8,
            hint = "Field",
            fieldType = "input",
            keyboard = "text",
            required = JsonPrimitive("true"),
            isActive = true,
            icon = null
        )

        val result = dto.toFormField()

        assertTrue(result.isRequired)
    }

    @Test
    fun `toFormField handles string false required correctly`() {
        val dto = FormFieldDto(
            fieldId = 9,
            hint = "Field",
            fieldType = "input",
            keyboard = "text",
            required = JsonPrimitive("false"),
            isActive = true,
            icon = null
        )

        val result = dto.toFormField()

        assertFalse(result.isRequired)
    }

    @Test
    fun `toFormSections maps list of sections correctly`() {
        val sections = listOf(
            listOf(
                FormFieldDto(
                    fieldId = 1,
                    hint = "UserName",
                    fieldType = "input",
                    keyboard = "text",
                    required = JsonPrimitive(true),
                    isActive = true,
                    icon = null
                )
            ),
            listOf(
                FormFieldDto(
                    fieldId = 2,
                    hint = "Birthday",
                    fieldType = "chooser",
                    keyboard = null,
                    required = JsonPrimitive(false),
                    isActive = true,
                    icon = null
                ),
                FormFieldDto(
                    fieldId = 3,
                    hint = "Gender",
                    fieldType = "chooser",
                    keyboard = null,
                    required = JsonPrimitive("false"),
                    isActive = true,
                    icon = null
                )
            )
        )

        val result = sections.toFormSections()

        assertEquals(2, result.size)
        assertEquals(0, result[0].id)
        assertEquals(1, result[0].fields.size)
        assertEquals(1, result[1].id)
        assertEquals(2, result[1].fields.size)
    }

    @Test
    fun `toFormField handles unknown field type as input`() {
        val dto = FormFieldDto(
            fieldId = 10,
            hint = "Field",
            fieldType = "unknown",
            keyboard = "text",
            required = JsonPrimitive(false),
            isActive = true,
            icon = null
        )

        val result = dto.toFormField()

        assertEquals(FieldType.INPUT, result.fieldType)
    }

    @Test
    fun `toFormField handles case insensitive field type`() {
        val dto = FormFieldDto(
            fieldId = 11,
            hint = "Field",
            fieldType = "CHOOSER",
            keyboard = "text",
            required = JsonPrimitive(false),
            isActive = true,
            icon = null
        )

        val result = dto.toFormField()

        assertEquals(FieldType.CHOOSER, result.fieldType)
    }

    @Test
    fun `toFormField handles case insensitive keyboard type`() {
        val dto = FormFieldDto(
            fieldId = 12,
            hint = "Field",
            fieldType = "input",
            keyboard = "NUMBER",
            required = JsonPrimitive(false),
            isActive = true,
            icon = null
        )

        val result = dto.toFormField()

        assertEquals(KeyboardType.NUMBER, result.keyboardType)
    }
}
