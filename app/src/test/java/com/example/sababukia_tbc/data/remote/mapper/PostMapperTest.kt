package com.example.sababukia_tbc.data.remote.mapper

import com.example.sababukia_tbc.data.remote.dto.PostDto
import org.junit.Assert.assertEquals
import org.junit.Test

class PostMapperTest {

    @Test
    fun `toDomain maps PostDto to Post correctly`() {
        val dto = PostDto(
            id = 1,
            avatar = "https://example.com/profile.jpg",
            postDate = 1234567890000L,
            firstName = "John",
            lastName = "Doe",
            images = listOf("img1", "img2"),
            commentsCount = 5,
            likesCount = 10,
            postDesc = "Test Post"
        )

        val result = dto.toDomain()

        assertEquals(1, result.id)
        assertEquals("John", result.owner.firstName)
        assertEquals("Doe", result.owner.lastName)
        assertEquals("John Doe", result.owner.fullName)
        assertEquals("https://example.com/profile.jpg", result.owner.profile)
        assertEquals(listOf("img1", "img2"), result.images)
        assertEquals(1234567890L, result.postDate) // Converted from ms to seconds
        assertEquals("Test Post", result.title)
        assertEquals(5, result.comments)
        assertEquals(10, result.likes)
    }

    @Test
    fun `toDomain handles null avatar`() {
        val dto = PostDto(
            id = 1,
            avatar = null,
            postDate = 100000L,
            firstName = "Jane",
            lastName = "Smith",
            images = emptyList(),
            commentsCount = 0,
            likesCount = 0,
            postDesc = "Post"
        )

        val result = dto.toDomain()

        assertEquals("", result.owner.profile)
    }

    @Test
    fun `toDomain handles null postDesc`() {
        val dto = PostDto(
            id = 1,
            avatar = "profile",
            postDate = 100000L,
            firstName = "A",
            lastName = "B",
            images = emptyList(),
            commentsCount = 0,
            likesCount = 0,
            postDesc = null
        )

        val result = dto.toDomain()

        assertEquals("", result.title)
    }

    @Test
    fun `toDomain maps list of PostDto to list of Post correctly`() {
        val dtos = listOf(
            PostDto(
                id = 1,
                avatar = "profile1",
                postDate = 100000L,
                firstName = "A",
                lastName = "B",
                images = emptyList(),
                commentsCount = 1,
                likesCount = 2,
                postDesc = "Post 1"
            ),
            PostDto(
                id = 2,
                avatar = "profile2",
                postDate = 200000L,
                firstName = "C",
                lastName = "D",
                images = listOf("img"),
                commentsCount = 3,
                likesCount = 4,
                postDesc = "Post 2"
            )
        )

        val result = dtos.toDomain()

        assertEquals(2, result.size)
        assertEquals(1, result[0].id)
        assertEquals("A B", result[0].owner.fullName)
        assertEquals(2, result[1].id)
        assertEquals("C D", result[1].owner.fullName)
    }
}
