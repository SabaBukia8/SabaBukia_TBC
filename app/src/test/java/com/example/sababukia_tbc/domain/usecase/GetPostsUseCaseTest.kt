package com.example.sababukia_tbc.domain.usecase

import com.example.sababukia_tbc.domain.model.FeedError
import com.example.sababukia_tbc.domain.model.Post
import com.example.sababukia_tbc.domain.model.Result
import com.example.sababukia_tbc.domain.repository.FeedRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GetPostsUseCaseTest {

    private lateinit var repository: FeedRepository
    private lateinit var useCase: GetPostsUseCase

    @Before
    fun setUp() {
        repository = mockk()
        useCase = GetPostsUseCase(repository)
    }

    @Test
    fun `invoke returns success when repository returns posts`() = runTest {
        val posts = listOf(
            Post(
                id = 1,
                owner = Post.Owner("John", "Doe", "profile1"),
                images = listOf("img1"),
                postDate = 1234567890L,
                title = "Post 1",
                comments = 5,
                likes = 10
            )
        )
        coEvery { repository.getPosts() } returns Result.Success(posts)

        val result = useCase()

        assertTrue(result is Result.Success)
        assertEquals(posts, (result as Result.Success).data)
    }

    @Test
    fun `invoke returns error when repository returns network error`() = runTest {
        coEvery { repository.getPosts() } returns Result.Error(FeedError.Network)

        val result = useCase()

        assertTrue(result is Result.Error)
        assertEquals(FeedError.Network, (result as Result.Error).error)
    }

    @Test
    fun `invoke returns error when repository returns unknown error`() = runTest {
        val errorMessage = "Something went wrong"
        coEvery { repository.getPosts() } returns Result.Error(FeedError.Unknown(errorMessage))

        val result = useCase()

        assertTrue(result is Result.Error)
        val error = (result as Result.Error).error as FeedError.Unknown
        assertEquals(errorMessage, error.message)
    }
}
