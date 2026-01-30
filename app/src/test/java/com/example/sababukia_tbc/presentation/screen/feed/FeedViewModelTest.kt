package com.example.sababukia_tbc.presentation.screen.feed

import app.cash.turbine.test
import com.example.sababukia_tbc.domain.model.FeedError
import com.example.sababukia_tbc.domain.model.Post
import com.example.sababukia_tbc.domain.model.Result
import com.example.sababukia_tbc.domain.model.Story
import com.example.sababukia_tbc.domain.usecase.GetPostsUseCase
import com.example.sababukia_tbc.domain.usecase.GetStoriesUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FeedViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var getStoriesUseCase: GetStoriesUseCase
    private lateinit var getPostsUseCase: GetPostsUseCase

    private val testStories = listOf(
        Story(id = 1, cover = "cover1", title = "Story 1"),
        Story(id = 2, cover = "cover2", title = "Story 2")
    )

    private val testPosts = listOf(
        Post(
            id = 1,
            owner = Post.Owner("John", "Doe", "profile1"),
            images = listOf("img1"),
            postDate = 1234567890L,
            title = "Post 1",
            comments = 5,
            likes = 10,
            canComment = true,
            canPostPhoto = false
        )
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        getStoriesUseCase = mockk()
        getPostsUseCase = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state shows loading and fetches data`() = runTest {
        coEvery { getStoriesUseCase() } returns Result.Success(testStories)
        coEvery { getPostsUseCase() } returns Result.Success(testPosts)

        val viewModel = FeedViewModel(getStoriesUseCase, getPostsUseCase)

        viewModel.state.test {
            val initialState = awaitItem()
            assertTrue(initialState.isLoading)

            testDispatcher.scheduler.advanceUntilIdle()

            val loadedState = awaitItem()
            assertFalse(loadedState.isLoading)
            assertEquals(testStories, loadedState.stories)
            assertEquals(testPosts, loadedState.posts)
            assertNull(loadedState.storiesError)
            assertNull(loadedState.postsError)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `shows error when stories fetch fails`() = runTest {
        coEvery { getStoriesUseCase() } returns Result.Error(FeedError.Network)
        coEvery { getPostsUseCase() } returns Result.Success(testPosts)

        val viewModel = FeedViewModel(getStoriesUseCase, getPostsUseCase)

        viewModel.state.test {
            awaitItem()
            testDispatcher.scheduler.advanceUntilIdle()

            val state = awaitItem()
            assertFalse(state.isLoading)
            assertEquals("Network error. Please check your connection.", state.storiesError)
            assertEquals(testPosts, state.posts)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `shows error when posts fetch fails`() = runTest {
        coEvery { getStoriesUseCase() } returns Result.Success(testStories)
        coEvery { getPostsUseCase() } returns Result.Error(FeedError.Unknown("Server error"))

        val viewModel = FeedViewModel(getStoriesUseCase, getPostsUseCase)

        viewModel.state.test {
            awaitItem()
            testDispatcher.scheduler.advanceUntilIdle()

            val state = awaitItem()
            assertFalse(state.isLoading)
            assertEquals(testStories, state.stories)
            assertEquals("Server error", state.postsError)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `retry event reloads feed`() = runTest {
        coEvery { getStoriesUseCase() } returns Result.Error(FeedError.Network) andThen Result.Success(testStories)
        coEvery { getPostsUseCase() } returns Result.Error(FeedError.Network) andThen Result.Success(testPosts)

        val viewModel = FeedViewModel(getStoriesUseCase, getPostsUseCase)

        viewModel.state.test {
            awaitItem() // initial loading
            testDispatcher.scheduler.advanceUntilIdle()
            awaitItem() // error state

            viewModel.onEvent(FeedEvent.Retry)

            val retryLoadingState = awaitItem()
            assertTrue(retryLoadingState.isLoading)

            testDispatcher.scheduler.advanceUntilIdle()

            val successState = awaitItem()
            assertFalse(successState.isLoading)
            assertEquals(testStories, successState.stories)
            assertEquals(testPosts, successState.posts)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `emits side effect on error`() = runTest {
        coEvery { getStoriesUseCase() } returns Result.Error(FeedError.Network)
        coEvery { getPostsUseCase() } returns Result.Success(testPosts)

        val viewModel = FeedViewModel(getStoriesUseCase, getPostsUseCase)

        viewModel.sideEffect.test {
            testDispatcher.scheduler.advanceUntilIdle()

            val effect = awaitItem()
            assertTrue(effect is FeedSideEffect.ShowError)
            assertEquals("Network error. Please check your connection.", (effect as FeedSideEffect.ShowError).message)

            cancelAndIgnoreRemainingEvents()
        }
    }
}
