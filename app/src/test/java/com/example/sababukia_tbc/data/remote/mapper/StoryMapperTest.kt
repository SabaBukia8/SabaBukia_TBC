package com.example.sababukia_tbc.data.remote.mapper

import com.example.sababukia_tbc.data.remote.dto.StoryDto
import org.junit.Assert.assertEquals
import org.junit.Test

class StoryMapperTest {

    @Test
    fun `toDomain maps StoryDto to Story correctly`() {
        val dto = StoryDto(
            id = 1,
            cover = "https://example.com/cover.jpg",
            title = "Test Story"
        )

        val result = dto.toDomain()

        assertEquals(1, result.id)
        assertEquals("https://example.com/cover.jpg", result.cover)
        assertEquals("Test Story", result.title)
    }

    @Test
    fun `toDomain maps list of StoryDto to list of Story correctly`() {
        val dtos = listOf(
            StoryDto(id = 1, cover = "cover1", title = "Story 1"),
            StoryDto(id = 2, cover = "cover2", title = "Story 2"),
            StoryDto(id = 3, cover = "cover3", title = "Story 3")
        )

        val result = dtos.toDomain()

        assertEquals(3, result.size)
        assertEquals(1, result[0].id)
        assertEquals("cover1", result[0].cover)
        assertEquals("Story 1", result[0].title)
        assertEquals(2, result[1].id)
        assertEquals(3, result[2].id)
    }

    @Test
    fun `toDomain maps empty list correctly`() {
        val dtos = emptyList<StoryDto>()

        val result = dtos.toDomain()

        assertEquals(0, result.size)
    }
}
