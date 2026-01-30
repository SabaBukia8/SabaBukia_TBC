package com.example.sababukia_tbc.domain.usecase

import com.example.sababukia_tbc.domain.model.FeedError
import com.example.sababukia_tbc.domain.model.Result
import com.example.sababukia_tbc.domain.model.Story
import com.example.sababukia_tbc.domain.repository.FeedRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GetStoriesUseCaseTest {

    private lateinit var repository: FeedRepository
    private lateinit var useCase: GetStoriesUseCase

    @Before
    fun setUp() {
        repository = mockk()
        useCase = GetStoriesUseCase(repository)
    }

    @Test
    fun `invoke returns success when repository returns stories`() = runTest {
        val stories = listOf(
            Story(id = 1, cover = "url1", title = "Story 1"),
            Story(id = 2, cover = "url2", title = "Story 2")
        )
        coEvery { repository.getStories() } returns Result.Success(stories)

        val result = useCase()

        assertTrue(result is Result.Success)
        assertEquals(stories, (result as Result.Success).data)
    }

    @Test
    fun `invoke returns error when repository returns network error`() = runTest {
        coEvery { repository.getStories() } returns Result.Error(FeedError.Network)

        val result = useCase()

        assertTrue(result is Result.Error)
        assertEquals(FeedError.Network, (result as Result.Error).error)
    }

    @Test
    fun `invoke returns error when repository returns unknown error`() = runTest {
        val errorMessage = "Something went wrong"
        coEvery { repository.getStories() } returns Result.Error(FeedError.Unknown(errorMessage))

        val result = useCase()

        assertTrue(result is Result.Error)
        val error = (result as Result.Error).error as FeedError.Unknown
        assertEquals(errorMessage, error.message)
    }
}
