package com.example.sababukia_tbc.presentation.statistics

import app.cash.turbine.test
import com.example.sababukia_tbc.domain.model.Result
import com.example.sababukia_tbc.domain.model.WorkspaceError
import com.example.sababukia_tbc.domain.model.WorkspaceItem
import com.example.sababukia_tbc.domain.usecase.GetWorkspacesUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
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
class StatisticsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var getWorkspacesUseCase: GetWorkspacesUseCase

    private val sampleWorkspaces = listOf(
        WorkspaceItem(
            location = "Tbilisi",
            altitudeM = 500,
            title = "Office A",
            image = "https://example.com/a.jpg",
            stars = 4,
            price = 100
        ),
        WorkspaceItem(
            location = "Batumi",
            altitudeM = 10,
            title = "Office B",
            image = "https://example.com/b.jpg",
            stars = 5,
            price = 200
        )
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        getWorkspacesUseCase = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `load workspaces successfully updates state with items`() = runTest {
        coEvery { getWorkspacesUseCase() } returns Result.Success(sampleWorkspaces)

        val viewModel = StatisticsViewModel(getWorkspacesUseCase)
        advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals(sampleWorkspaces, state.workspaces)
        assertFalse(state.isLoading)
        assertNull(state.error)
    }

    @Test
    fun `network error updates state with error message`() = runTest {
        coEvery { getWorkspacesUseCase() } returns Result.Error(WorkspaceError.Network)

        val viewModel = StatisticsViewModel(getWorkspacesUseCase)
        advanceUntilIdle()

        val state = viewModel.state.value
        assertTrue(state.workspaces.isEmpty())
        assertFalse(state.isLoading)
        assertEquals("Network error. Please check your connection.", state.error)
    }

    @Test
    fun `LoadWorkspaces event retries fetch`() = runTest {
        coEvery { getWorkspacesUseCase() } returns Result.Error(WorkspaceError.Network)

        val viewModel = StatisticsViewModel(getWorkspacesUseCase)
        advanceUntilIdle()

        coEvery { getWorkspacesUseCase() } returns Result.Success(sampleWorkspaces)
        viewModel.onEvent(StatisticsEvent.LoadWorkspaces)
        advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals(sampleWorkspaces, state.workspaces)
        assertFalse(state.isLoading)
        assertNull(state.error)
    }

    @Test
    fun `loading state transitions from true to false on success`() = runTest {
        coEvery { getWorkspacesUseCase() } returns Result.Success(sampleWorkspaces)

        val viewModel = StatisticsViewModel(getWorkspacesUseCase)

        viewModel.state.test {
            advanceUntilIdle()
            cancelAndConsumeRemainingEvents()
        }

        val finalState = viewModel.state.value
        assertFalse(finalState.isLoading)
        assertEquals(sampleWorkspaces, finalState.workspaces)
    }

    @Test
    fun `side effect emitted on network error`() = runTest {
        coEvery { getWorkspacesUseCase() } returns Result.Error(WorkspaceError.Network)

        val viewModel = StatisticsViewModel(getWorkspacesUseCase)

        viewModel.sideEffect.test {
            advanceUntilIdle()
            val effect = awaitItem()
            assertTrue(effect is StatisticsSideEffect.ShowError)
            assertEquals(
                "Network error. Please check your connection.",
                (effect as StatisticsSideEffect.ShowError).message
            )
        }
    }

    @Test
    fun `unknown error updates state with custom message`() = runTest {
        val errorMessage = "Something went wrong"
        coEvery { getWorkspacesUseCase() } returns Result.Error(WorkspaceError.Unknown(errorMessage))

        val viewModel = StatisticsViewModel(getWorkspacesUseCase)
        advanceUntilIdle()

        val state = viewModel.state.value
        assertTrue(state.workspaces.isEmpty())
        assertFalse(state.isLoading)
        assertEquals(errorMessage, state.error)
    }

    @Test
    fun `side effect emitted on unknown error`() = runTest {
        val errorMessage = "Unexpected failure"
        coEvery { getWorkspacesUseCase() } returns Result.Error(WorkspaceError.Unknown(errorMessage))

        val viewModel = StatisticsViewModel(getWorkspacesUseCase)

        viewModel.sideEffect.test {
            advanceUntilIdle()
            val effect = awaitItem()
            assertTrue(effect is StatisticsSideEffect.ShowError)
            assertEquals(errorMessage, (effect as StatisticsSideEffect.ShowError).message)
        }
    }

    @Test
    fun `initial state is loading true before result arrives`() = runTest {
        coEvery { getWorkspacesUseCase() } coAnswers {
            kotlinx.coroutines.delay(1000)
            Result.Success(sampleWorkspaces)
        }

        val viewModel = StatisticsViewModel(getWorkspacesUseCase)
        advanceTimeBy(100)

        val loadingState = viewModel.state.value
        assertTrue(loadingState.isLoading)
        assertTrue(loadingState.workspaces.isEmpty())
        assertNull(loadingState.error)

        advanceUntilIdle()

        val finalState = viewModel.state.value
        assertFalse(finalState.isLoading)
        assertEquals(sampleWorkspaces, finalState.workspaces)
    }

    @Test
    fun `success with empty list updates state with no workspaces`() = runTest {
        coEvery { getWorkspacesUseCase() } returns Result.Success(emptyList())

        val viewModel = StatisticsViewModel(getWorkspacesUseCase)
        advanceUntilIdle()

        val state = viewModel.state.value
        assertTrue(state.workspaces.isEmpty())
        assertFalse(state.isLoading)
        assertNull(state.error)
    }

    @Test
    fun `LoadWorkspaces event sets loading true then resolves`() = runTest {
        coEvery { getWorkspacesUseCase() } coAnswers {
            kotlinx.coroutines.delay(1000)
            Result.Success(sampleWorkspaces)
        }

        val viewModel = StatisticsViewModel(getWorkspacesUseCase)
        advanceUntilIdle()

        viewModel.onEvent(StatisticsEvent.LoadWorkspaces)
        advanceTimeBy(100)

        val loadingState = viewModel.state.value
        assertTrue(loadingState.isLoading)

        advanceUntilIdle()

        val finalState = viewModel.state.value
        assertFalse(finalState.isLoading)
        assertEquals(sampleWorkspaces, finalState.workspaces)
    }
}
